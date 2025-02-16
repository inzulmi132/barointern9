package com.sparta.barointern9.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponseDto {
    private final String grantType;
    private final String accessToken;
    private final String refreshToken;
}
