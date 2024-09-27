package com.acoldbottle.todolist.jwt;

import com.acoldbottle.todolist.domain.UserRole;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰을 생성, 검증하는 유틸 클래스
 */
@Component
public class JWTUtil {

    private final SecretKey secretKey; // JWT 서명에 사용되는 비밀 키

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * JWT 토큰에서 사용자 이름 추출
     */
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
    }

    /**
     * JWT 토큰에서 사용자 역할 추출
     */
    public UserRole getRole(String token) {

        String roleString = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
        return UserRole.valueOf(roleString);
    }

    /**
     * JWT 토큰에서 카테고리(access) 추출
     */
    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("access", String.class);
    }

    /**
     * JWT 토큰의 만료여부 체크
     */
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    /**
     * JWT 생성
     */
    public String createJwt(String category, String username, UserRole role, Long userId,Long expiredMs) {

        return Jwts.builder()
                .claim("access", category)
                .claim("username", username)
                .claim("role", role.name())
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
