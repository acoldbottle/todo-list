package com.acoldbottle.todolist.jwt;

/**
 * access, refresh 토큰 만료시간.
 */
public class TokenExpiration {

    public static final long ACCESS_TOKEN_EXPIRATION = 10 * 60 * 1000L; // 10분
    public static final long REFRESH_TOKEN_EXPIRATION = 14 * 24 * 60 * 60 * 1000L; // 14일
}
