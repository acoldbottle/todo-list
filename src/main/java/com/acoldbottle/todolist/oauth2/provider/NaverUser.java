package com.acoldbottle.todolist.oauth2.provider;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 네이버로 로그인한 유저 정보 추출
 */
@RequiredArgsConstructor
public class NaverUser implements Oauth2UserResponse {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
