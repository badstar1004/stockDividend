package com.stock.stockdividend.sevice;

import com.stock.stockdividend.exception.imp.CompanyException;
import com.stock.stockdividend.model.Company;
import com.stock.stockdividend.model.Dividend;
import com.stock.stockdividend.model.ScrapedResult;
import com.stock.stockdividend.model.constants.CacheKey;
import com.stock.stockdividend.persist.CompanyRepository;
import com.stock.stockdividend.persist.DividendRepository;
import com.stock.stockdividend.persist.entity.CompanyEntity;
import com.stock.stockdividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    /**
     * 회사명 기준 배당금 조회
     *
     * @param companyName
     * @return
     */
    // 캐싱 적합한지?
    // 요청이 자주 들어오는가? -> 동일한 데이터 요청이 많음
    // 자주 변경되는 데이터인가? -> 자주 변경 x / 추가정보가 업데이트되지만 자주는 아님
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("Search Company -> " + companyName);
        // 회사명 기준 회사 조회
        // 결과가 없을 수 있음
        CompanyEntity companyEntity =
                this.companyRepository.findByName(companyName)
                        .orElseThrow(() -> new CompanyException("NEC"));

        // 조회된 회사 id 로 배당금 조회
        List<DividendEntity> dividendEntities =
                this.dividendRepository.findAllByCompanyId(companyEntity.getId());

        // 조회된 회사 정보 + 배당금 반환
        List<Dividend> dividendList = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());

        return new ScrapedResult(
                new Company(companyEntity.getTicker(), companyEntity.getName()),
                dividendList);
    }
}
