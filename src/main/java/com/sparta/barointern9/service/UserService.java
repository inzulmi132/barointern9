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
import com.sparta.barointern9.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final JwtUtil jwtUtil;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        if(userRepository.existsByUsername(requestDto.getUsername())) {
            throw new CustomApiException(ErrorCode.ALREADY_EXIST_USERNAME);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = requestDto.toUser(encodedPassword);
        user = userRepository.save(user);

        Authority authority = authorityService.createAuthority(user, UserRole.ROLE_USER);

        return new SignupResponseDto(user, List.of(authority));
    }

    @Transactional
    public void signoff(User user) {
        authorityService.deleteAuthorityByUser(user);
        userRepository.delete(user);
    }

    public TokenResponseDto refresh(RefreshRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();
        jwtUtil.validateToken(refreshToken);

        String username = jwtUtil.getSubjectFromToken(refreshToken);
        return jwtUtil.generateTokens(username);
    }
}
