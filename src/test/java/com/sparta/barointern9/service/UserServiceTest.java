package com.sparta.barointern9.service;

import com.sparta.barointern9.dto.request.RefreshRequestDto;
import com.sparta.barointern9.dto.request.SignupRequestDto;
import com.sparta.barointern9.dto.response.SignupResponseDto;
import com.sparta.barointern9.dto.response.TokenResponseDto;
import com.sparta.barointern9.entity.Authority;
import com.sparta.barointern9.entity.User;
import com.sparta.barointern9.enums.UserRole;
import com.sparta.barointern9.exception.CustomApiException;
import com.sparta.barointern9.exception.ErrorCode;
import com.sparta.barointern9.jwt.JwtUtil;
import com.sparta.barointern9.repository.AuthorityRepository;
import com.sparta.barointern9.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("signup 테스트 1")
    void test1() {
        // given
        String username = "test";
        String password = "1234";
        String nickname = "test";
        String encodedPassword = new BCryptPasswordEncoder().encode(password);

        SignupRequestDto requestDto = SignupRequestDto.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();
        User user = requestDto.toUser(encodedPassword);
        Authority authority = Authority.builder()
                .user(user)
                .userRole(UserRole.ROLE_USER)
                .build();

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any())).thenReturn(user);
        when(authorityRepository.save(any())).thenReturn(authority);

        // when
        SignupResponseDto result = userService.signup(requestDto);

        // then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(nickname, result.getNickname());
        assertEquals(UserRole.ROLE_USER.toString(), result.getAuthorities().get(0).getAuthorityName());
    }

    @Test
    @DisplayName("signup 테스트 2")
    void test2() {
        String username = "test";
        String password = "1234";
        String nickname = "test";

        SignupRequestDto requestDto = SignupRequestDto.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();

        when(userRepository.existsByUsername(username)).thenReturn(true);

        // when - then
        CustomApiException exception = assertThrows(CustomApiException.class, () -> userService.signup(requestDto));
        assertEquals(exception.getErrorCode(), ErrorCode.ALREADY_EXIST_USERNAME);
    }

    @Test
    @DisplayName("signoff 테스트")
    void test3() {
        // given
        User user = User.builder().build();

        doNothing().when(authorityRepository).deleteAllByUser(user);
        doNothing().when(userRepository).delete(user);

        // when - then
        userService.signoff(user);
    }

    @Test
    @DisplayName("refresh 테스트")
    void test4() {
        // given
        String refreshToken = "refreshToken";
        String username = "test";
        String grantType = "Bearer ";
        String createdAccessToken = "createdAccessToken";
        String createdRefreshToken = "createdRefreshToken";

        RefreshRequestDto requestDto = RefreshRequestDto.builder()
                .refreshToken(refreshToken)
                .build();
        TokenResponseDto responseDto = TokenResponseDto.builder()
                .grantType(grantType)
                .accessToken(createdAccessToken)
                .refreshToken(createdRefreshToken)
                .build();

        doNothing().when(jwtUtil).validateToken(any());
        when(jwtUtil.getSubjectFromToken(refreshToken)).thenReturn(username);
        when(jwtUtil.generateTokenResponseDto(username)).thenReturn(responseDto);

        // when
        TokenResponseDto result = userService.refresh(requestDto);

        // then
        assertNotNull(result);
        assertEquals(grantType, result.getGrantType());
        assertEquals(createdAccessToken, result.getAccessToken());
        assertEquals(createdRefreshToken, result.getRefreshToken());
    }
}
