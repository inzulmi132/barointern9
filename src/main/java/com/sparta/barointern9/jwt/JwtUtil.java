package com.sparta.barointern9.jwt;

import com.sparta.barointern9.dto.response.TokenResponseDto;
import com.sparta.barointern9.exception.CustomApiException;
import com.sparta.barointern9.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    public static final String ACCESS_TOKEN_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30분
    public static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 3; // 3일

    private final Key key;

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateAccessToken(String subject) {
        Date expiration = new Date(new Date().getTime() + ACCESS_TOKEN_EXPIRATION);
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        Date expiration = new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRATION);
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch(SecurityException | MalformedJwtException e) {
            throw new CustomApiException(ErrorCode.INVALID_JWT_SIGN);
        } catch(ExpiredJwtException e) {
            throw new CustomApiException(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch(UnsupportedJwtException e) {
            throw new CustomApiException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch(IllegalArgumentException e) {
            throw new CustomApiException(ErrorCode.WRONG_JWT_TOKEN);
        }
    }

    public String getSubjectFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getAccessTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(ACCESS_TOKEN_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public TokenResponseDto generateToken(String username) {
        return new TokenResponseDto(
                BEARER_PREFIX,
                generateAccessToken(username),
                generateRefreshToken(username)
        );
    }
}
