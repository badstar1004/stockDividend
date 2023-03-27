package com.stock.stockdividend.exception.imp;

import com.stock.stockdividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class UserException extends AbstractException {

    private String exceptionGbn = "";

    public UserException(String exceptionGbn){
        this.exceptionGbn = exceptionGbn;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        String messaage = "";

        switch (exceptionGbn){
            case "NEI":     // 존재하지 않는 ID
                messaage = "존재하지 않는 아이디입니다.";
                break;
            case "EI":      // 중복 추가 시
                messaage = "이미 가입된 아이디입니다.";
                break;
            case "PMM":      // 틀린 패스워드
                messaage = "비밀번호가 일치하지 않습니다.";
                break;
        }
        return messaage;
    }
}
