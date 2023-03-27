package com.stock.stockdividend.exception.imp;

import com.stock.stockdividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class CompanyException extends AbstractException {

    private String exceptionGbn = "";

    /**
     * 생성자
     *
     * @param exceptionGbn 예외 구분자
     */
    public CompanyException(String exceptionGbn) {
        this.exceptionGbn = exceptionGbn;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {

        String messaage = "";

        switch (exceptionGbn) {
            case "NEC":     // 존재하지 않는 회사
                messaage = "존재하지 않는 회사입니다.";
                break;
            case "EC":      // 중복 추가 시
                messaage = "이미 추가된 회사입니다.";
                break;
            case "TNULL":   // Ticker 빈문자열 일 경우
                messaage = "Ticker 를 입력하세요";
                break;
        }
        return messaage;
    }
}
