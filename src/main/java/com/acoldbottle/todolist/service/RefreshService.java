package com.acoldbottle.todolist.service;

import com.acoldbottle.todolist.domain.RefreshToken;
import com.acoldbottle.todolist.domain.UserRole;
import com.acoldbottle.todolist.jwt.JWTUtil;
import com.acoldbottle.todolist.jwt.TokenExpiration;
import com.acoldbottle.todolist.repository.RefreshRepository;
import com.acoldbottle.todolist.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Refresh 토큰 서비스
 * ======================================
 * reissue() => 검증 후에 access 토큰 재발급
 * ======================================
 * createCookie() => 쿠키 생성
 * addRefreshEntity() => refresh 토큰 저장
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String refresh = request.getHeader("refresh");

        // 리프레시 토큰이 null인지 확인
        if (refresh == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"message\": \"Refresh token is required.\"}");
            return;
        }

        // 리프레시 토큰의 만료 여부 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            log.info("Refresh token is expired. Redirecting to login.");
            log.error("Expired JWT exception: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"message\": \"Refresh token is expired. Please log in again.\", \"redirect\": \"/login\"}");
            return;
        }

        String username = jwtUtil.getUsername(refresh);
        UserRole role = jwtUtil.getRole(refresh);
        Long userId = jwtUtil.getUserId(refresh);

        String newAccess = jwtUtil.createJwt("access", username, role, userId, TokenExpiration.ACCESS_TOKEN_EXPIRATION);

        log.info("Access 토큰 재발급");

        // 리프레시 엔티티 저장
        addRefreshEntity(username, refresh);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("access", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", refresh));

        String jsonResponse = String.format("{\"userId\": \"%s\", \"username\": \"%s\", \"access\": \"Bearer %s\", \"refresh\": \"%s\"}",
                userId, username, newAccess, refresh);

        response.getWriter().write(jsonResponse);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshEntity(String username, String refresh) {
        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshRepository.save(refreshEntity);
    }
}
