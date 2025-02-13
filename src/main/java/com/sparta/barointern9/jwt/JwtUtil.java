package com.sparta.barointern9.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

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
}
