package com.acoldbottle.todolist.oauth2;

import com.acoldbottle.todolist.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * OAuth2 인증을 통해 얻은 사용자 정보를 담고 있는 클래스
 */
@Slf4j
public class CustomOauth2User implements OAuth2User {

    private final UserDTO userDTO;
    private Map<String, Object> attributes; // OAuth2 프로바이더에서 가져온 사용자 속성

    public CustomOauth2User(UserDTO userDTO) {

        this.userDTO = userDTO;
    }

    public CustomOauth2User(UserDTO userDTO, Map<String, Object> attributes) {

        this.userDTO = userDTO;
        this.attributes = attributes;
    }

    /**
     * OAuth2 사용자 속성 반환
     */
    @Override
    public Map<String, Object> getAttributes() {

        return attributes;
    }

    /**
     * OAuth2 사용자 권한 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.singletonList(() -> String.valueOf(userDTO.getRole()));
    }

    /**
     * OAuth2 사용자 이름 반환
     */
    @Override
    public String getName() {

        return userDTO.getUsername();
    }

    /**
     * OAuth2 사용자 아이디 반환
     */
    public Long getId() {

        if (userDTO.getUserId() == null) {

            log.warn("User Not Found, User ID is Null");
        }
        return userDTO.getUserId();
    }
}
