package com.sparta.barointern9.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomErrorResponseDto {

    private final HttpStatus status;
    private final String msg;

    public CustomErrorResponseDto(CustomApiException e) {
        this.status = e.getErrorCode().getStatus();
        this.msg = e.getErrorCode().getMessage();
    }
}
