package com.sparta.barointern9.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<CustomErrorResponseDto> apiException(CustomApiException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new CustomErrorResponseDto(e));
    }
}
