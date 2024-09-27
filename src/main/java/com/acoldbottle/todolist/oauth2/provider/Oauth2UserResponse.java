package com.acoldbottle.todolist.oauth2.provider;

/**
 * oauth2로 로그인한 유저 정보 추출 인터페이스
 */
public interface Oauth2UserResponse {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
}
