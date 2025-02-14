package com.sparta.barointern9.dto.response;

import com.sparta.barointern9.entity.Authority;
import com.sparta.barointern9.entity.User;
import lombok.Getter;

import java.util.List;

@Getter
public class SignupResponseDto {

    private final String username;
    private final String nickname;
    private final List<AuthorityResponseDto> authorities;

    public SignupResponseDto(User user, List<Authority> authorities) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.authorities = authorities.stream()
                .map(AuthorityResponseDto::new)
                .toList();
    }
}
