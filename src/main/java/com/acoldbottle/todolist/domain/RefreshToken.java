package com.acoldbottle.todolist.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import static com.acoldbottle.todolist.jwt.TokenExpiration.REFRESH_TOKEN_EXPIRATION;

/**
 * Redis 에서 hash 자료형으로 refresh 토큰과 사용자 이름 을 관리
 * key 는 "refresh_token", TTL 은 14일
 */
@RedisHash(value = "refresh_token", timeToLive = REFRESH_TOKEN_EXPIRATION)
@Getter @Setter
public class RefreshToken {

    @Id
    @Indexed
    private String refresh;
    private String username;
}

