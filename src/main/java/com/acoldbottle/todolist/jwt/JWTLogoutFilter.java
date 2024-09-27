package com.acoldbottle.todolist.jwt;


import com.acoldbottle.todolist.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * 로그아웃 했을때 실행되는 JWT 필터
 * 리프레시 토큰을 검증 후 삭제
 */
@Slf4j
@RequiredArgsConstructor
public class JWTLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    /**
     *  ServletRequest와 ServletResponse를 HttpServletRequest와 HttpServletResponse로 변환하여 doFilter 메서드 호출
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        doFilter((HttpServletRequest)request, (HttpServletResponse) response, filterChain);
    }

    /**
     * 로그아웃 요청 처리하는 필터
     */
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();

        // 로그아웃 요청이 아닌 경우 필터 체인 진행
        if (!requestURI.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }

        // POST 메소드가 아닌 경우 필터 체인 진행
        String method = request.getMethod();
        if (!method.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 리프레시 토큰 추출
        String refresh = request.getHeader("refresh");
        log.info("Received refresh token for logout: {}", refresh);

        // 리프레시 토큰이 없는 경우 400 BAD REQUEST 응답
        if (refresh == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Refresh token is null.");
            return;
        }

        // 리프레시 토큰 만료 여부 체크
        try {
            jwtUtil.isExpired(refresh);

        } catch (ExpiredJwtException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Expired JWT exception: {}", e.getMessage());
            return;
        }

        // 리프레시 토큰 카테고리 체크
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Invalid token category: {}", category);
            return;
        }

        // Redis에서 리프레시 토큰 존재 여부 체크
        String username = jwtUtil.getUsername(refresh);
        log.info("Extracted username from refresh token: {}", username);
        Boolean isExists = refreshRepository.existsByUsername(username);

        if (!isExists) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Refresh token does not exist in Redis.");
            return;
        }

        // Redis에서 리프레시 토큰 삭제
        refreshRepository.deleteByUsername(username);
        log.info("Deleted username: {}", username);

        // 삭제 후 존재 여부 확인
        Boolean isExistsAfterDelete = refreshRepository.existsByUsername(username);
        log.info("Does username exist after deletion? {}", isExistsAfterDelete);

        // 리프레시 쿠키 삭제
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0); // 쿠키 만료 설정
        cookie.setPath("/"); // 쿠키의 유효 경로 설정

        response.addCookie(cookie); // 쿠키 추가
        response.setStatus(HttpServletResponse.SC_OK); // 성공 응답
        response.getWriter().write("{\"message\": \"Logout successful.\"}"); // 응답 메시지 작성
    }
}
