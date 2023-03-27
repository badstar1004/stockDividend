package com.stock.stockdividend.scraper;

import com.stock.stockdividend.model.Company;
import com.stock.stockdividend.model.ScrapedResult;

public interface Scraper {

    /**
     * 스크래핑 메서드
     *
     * @param company
     * @return
     */
    ScrapedResult scrap(Company company);

    /**
     * 회사의 정보 조회
     *
     * @param ticker
     * @return
     */
    Company scrapCompanyByTicker(String ticker);

}
