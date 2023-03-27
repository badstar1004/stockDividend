package com.stock.stockdividend.sevice;

import com.stock.stockdividend.exception.imp.CompanyException;
import com.stock.stockdividend.model.Company;
import com.stock.stockdividend.model.ScrapedResult;
import com.stock.stockdividend.persist.CompanyRepository;
import com.stock.stockdividend.persist.DividendRepository;
import com.stock.stockdividend.persist.entity.CompanyEntity;
import com.stock.stockdividend.persist.entity.DividendEntity;
import com.stock.stockdividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Trie trie;


    /**
     * 저장
     *
     * @param ticker
     * @return
     */
    public Company save(String ticker) {
        // 회사 존재 여부
        boolean companyExists = companyRepository.existsByTicker(ticker);

        if (companyExists) {
            throw new CompanyException("EC");
        }

        return this.storeCompanyAndDividend(ticker);
    }

    /**
     * 모든 회사 정보 조회
     *
     * @param pageable
     * @return
     */
    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    /**
     * 회사정보 저장, 배당금 저장
     *
     * @param ticker
     * @return
     */
    private Company storeCompanyAndDividend(String ticker) {
        // ticker 기준 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

        // 해당 회사가 존재하면, 회사 배당금 스크래핑
        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        // 회사 정보 저장
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        // map 다른 값으로 매핍할 때 사용
        List<DividendEntity> dividendEntities =
                scrapedResult.getDividendEntities().stream()
                        .map(e -> new DividendEntity(companyEntity.getId(), e))
                        .collect(Collectors.toList());

        // 배당금 저장
        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

    /**
     * Like 연산자 사용
     *
     * @param keyword
     * @return
     */
    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);

        Page<CompanyEntity> companyEntities =
                this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);

        return companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }

    /**
     * 키워드 Trie 에 저장
     *
     * @param keyword
     */
    public void addAutoCompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    /**
     * 키워드로 Trie 에 저장된 회사 정보 조회
     *
     * @param keyword
     * @return
     */
    public List<String> autoComplete(String keyword) {
        // 최대 10개까지 보이도록 limit 사용
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 키워드로 Trie 에 저장된 정보 삭제
     *
     * @param keyword
     */
    public void deleteAutoCompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    /**
     * 회사 삭제
     *
     * @param ticker
     * @return
     */
    public String deleteCompany(String ticker) {

        // 회사 존재 여부 확인
        CompanyEntity companyEntity = this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new CompanyException("NEC"));

        // 배당금 삭제 후 회사 삭제
        this.dividendRepository.deleteByCompanyId(companyEntity.getId());
        this.companyRepository.delete(companyEntity);

        // 트라이에 있는 회사명 삭제
        this.deleteAutoCompleteKeyword(companyEntity.getName());

        return companyEntity.getName();
    }
}
