package com.stock.stockdividend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice       // 필터와 비슷하게 바깥쪽에서 접근 / 컨트롤러와 조금 더 가까운 레이어
public class CustomExceptionHandler {

    /**
     * 예외처리 핸들러 (어떻게 던져줄지 정하는 메서드)
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(e.getStatusCode()));
    }
}
