package com.acoldbottle.todolist.handler;

import com.acoldbottle.todolist.domain.RefreshToken;
import com.acoldbottle.todolist.domain.UserRole;
import com.acoldbottle.todolist.jwt.JWTUtil;
import com.acoldbottle.todolist.jwt.TokenExpiration;
import com.acoldbottle.todolist.repository.RefreshRepository;
import com.acoldbottle.todolist.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.acoldbottle.todolist.jwt.TokenExpiration.REFRESH_TOKEN_EXPIRATION;

/**
 * 로그인에 성공했을때 access, refresh 토큰을 발급하는 핸들러
 */
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;


    /**
     * 기존에 refresh 토큰이 만약 남아있다면 삭제
     *
     * access, refresh 토큰 생성. 각각 만료시간은 10분, 14일
     * access 토큰과 refresh 토큰을 페이지에 응답
     * access 토큰은 헤더, refresh 토큰은 쿠키에 보관
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        Long userId = userRepository.findByUsername(username).getId();

        refreshRepository.deleteByUsername(username);

        String access = jwtUtil.createJwt("access", username, UserRole.valueOf(role), userId, TokenExpiration.ACCESS_TOKEN_EXPIRATION);
        String refresh = jwtUtil.createJwt("refresh", username, UserRole.valueOf(role), userId, REFRESH_TOKEN_EXPIRATION);

        addRefreshEntity(username, refresh);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("access", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));

        String jsonResponse = String.format("{\"userId\": \"%s\", \"username\": \"%s\", \"access\": \"Bearer %s\", \"refresh\": \"%s\"}",
                userId, username, access, refresh);

        response.getWriter().write(jsonResponse);

    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);

        cookie.setMaxAge(60 * 60 * 24);
//        cookie.setSecure(true);
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
