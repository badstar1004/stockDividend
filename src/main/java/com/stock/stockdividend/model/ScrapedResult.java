package com.stock.stockdividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data       // 모든 모델 클래스에 붙이는 건 지양!
@AllArgsConstructor
public class ScrapedResult {

    private Company company;
    private List<Dividend> dividendEntities;

    /**
     * 생성자
     */
    public ScrapedResult() {
        this.dividendEntities = new ArrayList<>();
    }
}
