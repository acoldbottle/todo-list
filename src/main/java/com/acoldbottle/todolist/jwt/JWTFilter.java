package com.acoldbottle.todolist.jwt;

import com.acoldbottle.todolist.domain.UserRole;
import com.acoldbottle.todolist.dto.UserDTO;
import com.acoldbottle.todolist.oauth2.CustomOauth2User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 필터 클래스
 * HTTP 요청에 대한 액세스 토큰을 검증하고 인증 정보를 설정
 */
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    /**
     * 요청을 필터링하고 JWT 검증
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청 헤더에서 access 토큰 추출
        String authorization = request.getHeader("access");
        log.debug("access={}", authorization);

        // access 토큰이 없거나 "Bearer"로 시작하지 않는 경우
        if (authorization == null || !authorization.startsWith("Bearer")) {

            log.debug("token null");
            filterChain.doFilter(request, response);
            return;
        }

        // access 토큰에서 Bearer 분리 후에 토큰 만료 여부 체크
        String accessToken = authorization.split(" ")[1];
        try {

            jwtUtil.isExpired(accessToken);

        } catch (ExpiredJwtException e) {

            log.warn("token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰 카테고리 체크
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {

            log.warn("invalid access token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰에서 사용자 정보 추출
        String username = jwtUtil.getUsername(accessToken);
        UserRole role = jwtUtil.getRole(accessToken);
        Long userId = jwtUtil.getUserId(accessToken);

        // 사용자 DTO를 생성하고 CustomOauth2User 생성
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);
        userDTO.setUserId(userId);

        CustomOauth2User customOauth2User = new CustomOauth2User(userDTO);

        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customOauth2User, null, customOauth2User.getAuthorities());

        // SecurityContextHolder 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}

