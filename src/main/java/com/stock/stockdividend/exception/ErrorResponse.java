package com.stock.stockdividend.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {    // 예외가 발생했을때 던져줄 모델

    private int code;
    private String message;
}
