package com.sparta.barointern9.dto.request;

import com.sparta.barointern9.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignupRequestDto {

    private String username;
    private String password;
    private String nickname;

    public User toUser(String encodedPassword) {
        return User.builder()
                .username(this.username)
                .password(encodedPassword)
                .nickname(this.nickname)
                .build();
    }
}
