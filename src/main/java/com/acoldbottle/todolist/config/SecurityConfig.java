package com.acoldbottle.todolist.config;

import com.acoldbottle.todolist.handler.LoginSuccessHandler;
import com.acoldbottle.todolist.jwt.JWTFilter;
import com.acoldbottle.todolist.jwt.JWTLogoutFilter;
import com.acoldbottle.todolist.jwt.JWTUtil;
import com.acoldbottle.todolist.oauth2.CustomOAuth2UserService;
import com.acoldbottle.todolist.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Spring Security 설정 클래스
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(Collections.singletonList("access"));

                        return configuration;
                    }
                }));

        // CRSF 비활성화
        http
                .csrf((auth) -> auth.disable());

        // FORM LOGIN 비활성화
        http
                .formLogin((form) -> form.disable());

        // OAUTH2 로그인 설정
        http
                .oauth2Login((oauth) -> oauth
                        .userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)))
                        .successHandler(loginSuccessHandler));

        // JWT 필터 추가 (토큰 발급)
        http
                .addFilterAfter(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        // JWT 로그아웃 필터 추가 (토큰 삭제)
        http
                .addFilterBefore(new JWTLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        // 요청 권한 설정
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "/reissue").permitAll()
                        .requestMatchers("/api/**", "/logout").authenticated()
                        .anyRequest().denyAll());

        // 세션 관리 설정
        http
                .sessionManagement((auth) -> auth
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }
}
