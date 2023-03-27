package com.stock.stockdividend.scheduler;

import com.stock.stockdividend.model.Company;
import com.stock.stockdividend.model.ScrapedResult;
import com.stock.stockdividend.model.constants.CacheKey;
import com.stock.stockdividend.persist.CompanyRepository;
import com.stock.stockdividend.persist.DividendRepository;
import com.stock.stockdividend.persist.entity.CompanyEntity;
import com.stock.stockdividend.persist.entity.DividendEntity;
import com.stock.stockdividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@EnableCaching
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;

    private final DividendRepository dividendRepository;

    /**
     * 스케쥴러
     */
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    // finance 에 해당하는 캐시는 Redis 서버에서 다 비움
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        // 로그 저장
        log.info("Scraping Scheduler is Started");

        // 저장된 회사 정보 조회
        List<CompanyEntity> companyEntities = companyRepository.findAll();

        // 회사별 배당금 정보 조회
        for (CompanyEntity company : companyEntities) {
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName()));

            // 스크래핑한 배당금 정보 중 db에 없는 정보 저장
            // saveAll() 은 키 중복 에러 발생
            scrapedResult.getDividendEntities().stream()
                    // Dividend 모델을 DividendEntity 로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 존재하지 않는 경우에만 저장
                    .forEach(e -> {
                        boolean dataExists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());

                        if (!dataExists) {
                            log.info("insert new dividend -> " + e);
                            dividendRepository.save(e);
                        }
                    });

            // 스크래핑 대상 사이트가 부하되지 않도록 일시정지
            try {
                // 3초간 멈춤
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
