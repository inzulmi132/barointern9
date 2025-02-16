package com.sparta.barointern9.jwt;

import com.sparta.barointern9.exception.CustomApiException;
import com.sparta.barointern9.exception.ErrorCode;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;
import java.util.Date;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtUtilTest {
    private static JwtUtil jwtUtil;
    private String decodedSecretKey;

    @BeforeAll
    public void init() {
        decodedSecretKey = "barointern9SecretKeyForJwtUtilTestBeforeEncoding";
        String secretKey = Base64.getEncoder().encodeToString(decodedSecretKey.getBytes());
        jwtUtil = new JwtUtil(secretKey);
    }

    @Test
    @DisplayName("인코딩 테스트")
    public void test1() {
        assertEquals(decodedSecretKey, new String(Decoders.BASE64.decode(jwtUtil.getSecretKey())));
    }

    @Test
    @DisplayName("토큰 정상 발행 및 subject 테스트")
    public void test2() {
        // given
        String subject = "subject";
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10);

        // when
        String token = jwtUtil.generateToken(subject, expiration);

        // then
        assertNotNull(token);
        assertEquals(jwtUtil.getSubjectFromToken(token), subject);
    }

    @Test
    @DisplayName("토큰 검증 테스트 - 통과")
    public void test3() {
        // given
        String subject = "subject";
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10);

        // when
        String token = jwtUtil.generateToken(subject, expiration);

        // then
        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("토큰 검증 테스트 - 만료 일자가 지난 경우")
    public void test4() {
        // given
        String subject = "subject";
        // 1초전에 만료된 토큰 생성
        Date expiration = new Date(System.currentTimeMillis() - 1000 * 1);

        // when
        String token = jwtUtil.generateToken(subject, expiration);

        // then
        CustomApiException exception = assertThrows(CustomApiException.class, () -> jwtUtil.validateToken(token));
        assertEquals(exception.getErrorCode(), ErrorCode.EXPIRED_JWT_TOKEN);
    }

    @Test
    @DisplayName("토큰 검증 테스트 - 키 값이 잘못된 경우")
    public void test5() {
        // given
        String newDecodedSecretKey = "secondSecretKeyForJwtUtilTestBeforeEncoding";
        String newSecretKey = Base64.getEncoder().encodeToString(newDecodedSecretKey.getBytes());
        JwtUtil newJwtUtil = new JwtUtil(newSecretKey);

        String subject = "subject";
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10);

        // when
        String token = newJwtUtil.generateToken(subject, expiration);

        // then
        CustomApiException exception = assertThrows(CustomApiException.class, () -> jwtUtil.validateToken(token));
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_JWT_SIGN);
    }
}
