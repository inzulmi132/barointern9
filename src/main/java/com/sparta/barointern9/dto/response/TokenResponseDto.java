package com.sparta.barointern9.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {
    private final String grantType;
    private final String accessToken;
    private final String refreshToken;
}
