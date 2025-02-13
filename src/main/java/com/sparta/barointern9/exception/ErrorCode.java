package com.sparta.barointern9.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // jwt
    INVALID_JWT_SIGN(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명 입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰 입니다."),
    WRONG_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 JWT 토큰 입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰 입니다."),

    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
