package com.sparta.barointern9.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RefreshRequestDto {
    private String refreshToken;
}
