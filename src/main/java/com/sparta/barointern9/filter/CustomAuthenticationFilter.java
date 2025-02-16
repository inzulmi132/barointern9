package com.sparta.barointern9.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.barointern9.dto.request.SignRequestDto;
import com.sparta.barointern9.dto.response.TokenResponseDto;
import com.sparta.barointern9.exception.CustomApiException;
import com.sparta.barointern9.exception.CustomErrorResponseDto;
import com.sparta.barointern9.exception.ErrorCode;
import com.sparta.barointern9.jwt.JwtUtil;
import com.sparta.barointern9.userdetails.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public CustomAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/sign");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            SignRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), SignRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException {
        String username = ((UserDetailsImpl) auth.getPrincipal()).getUsername();
        TokenResponseDto responseDto = jwtUtil.generateTokenResponseDto(username);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        CustomApiException exception = new CustomApiException(ErrorCode.INCORRECT_USERNAME_OR_PASSWORD);
        CustomErrorResponseDto responseDto = new CustomErrorResponseDto(exception);

        response.setStatus(exception.getErrorCode().getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));
    }
}
