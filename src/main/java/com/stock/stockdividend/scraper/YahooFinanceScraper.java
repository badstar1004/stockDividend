package com.stock.stockdividend.scraper;

import com.stock.stockdividend.exception.imp.CompanyException;
import com.stock.stockdividend.model.Company;
import com.stock.stockdividend.model.Dividend;
import com.stock.stockdividend.model.ScrapedResult;
import com.stock.stockdividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component      // Bean 으로 사용
public class YahooFinanceScraper implements Scraper {

    // url 변수로 빼줌
    // String.format 사용 예정
    private static final String STATIC_URL =
            "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    // 회사명 url
    private static final String COMPANY_SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;   // 1일(60 * 60 * 24)

    /**
     * 스크래핑 메서드
     *
     * @param company
     * @return
     */
    @Override
    public ScrapedResult scrap(Company company) {
        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {

            long endTime = System.currentTimeMillis() / 1000;       // 현재날짜를 초로 환산
            String url = String.format(STATIC_URL, company.getTicker(), START_TIME, endTime);

            // Jsoup.connect 는 Connection 으로 리턴
            Connection connection = Jsoup.connect(url);

            // connection.get() 은 Document 으로 리턴
            Document document = connection.get();

            // document.getElementsByAttributeValue 는 여러개의 요소로 리턴해 Elements 으로 리턴
            Elements parsingDiviElements =
                    document.getElementsByAttributeValue("data-test", "historical-prices");

            // parsingDiviElements.get(0) 하나밖에 없어서 Element 로 리턴
            Element tableElement = parsingDiviElements.get(0);

            // tableElement 에 대해서 table 전체를 받아옴
            // table > thead(0) - tbody(1) - tfoot(2) 으로 구성됨
//            System.out.println(tableElement);
            Element tBody = tableElement.children().get(1);

            List<Dividend> listDividends = new ArrayList<>();

            // tBody 안에도 태그들이 많기 때문에 for 문으로 순회해서 가져옴
            for (Element e : tBody.children()) {
                // 배당금에 대한 데이터만
                String text = e.text();
                if (!text.endsWith("Dividend")) {
                    continue;
                }

                // 공백 기준으로 자름
                String[] splits = text.split(" ");
                int month = Month.strToNumber(splits[0]);       // 월
                int day = Integer.parseInt(splits[1].replace(",", ""));     // 일
                int year = Integer.parseInt(splits[2]);     // 년
                String dividend = splits[3];        // 배당금액

                // 값을 못찾을 경우 -1 리턴에 대한 예외처리
                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                listDividends.add(new Dividend(
                        LocalDateTime.of(year, month, day, 0, 0), dividend));
            }

            scrapedResult.setDividendEntities(listDividends);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return scrapedResult;
    }

    /**
     * 회사의 정보 조회
     *
     * @param ticker
     * @return
     */
    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(COMPANY_SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();

            // ticker 기준 회사가 없지만 검색은 됨 -> 예외처리
            Elements headings = document.select("h1");

            if (headings.size() == 0) {
                throw new CompanyException("NEC");
            }

            Element titleElement = document.getElementsByTag("h1").get(0);

            // Jsoup 버전에 따라 titleElement.text() 다름
            // 1.15.3 버전은 야후에 표시된 text 로 가져옴
            String title = titleElement.text().trim();

            return new Company(ticker, title);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
