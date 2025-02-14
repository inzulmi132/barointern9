package com.sparta.barointern9.dto.response;

import com.sparta.barointern9.entity.Authority;
import lombok.Getter;

@Getter
public class AuthorityResponseDto {

    private final String authorityName;

    public AuthorityResponseDto(Authority authority) {
        this.authorityName = authority.getUserRole().toString();
    }
}
