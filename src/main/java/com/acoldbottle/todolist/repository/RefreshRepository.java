package com.acoldbottle.todolist.repository;

import com.acoldbottle.todolist.domain.RefreshToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * Refresh 토큰 리포지토리
 * Redis 로 관리
 */
@Repository
public class RefreshRepository {

    private final RedisTemplate<String, RefreshToken> redisTemplate;

    public RefreshRepository(RedisTemplate<String, RefreshToken> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // username을 기준으로 존재 여부 확인
    public Boolean existsByUsername(String username) {
        return redisTemplate.opsForHash().hasKey("refresh_token", username);
    }

    // username을 기준으로 삭제
    public void deleteByUsername(String username) {
        redisTemplate.opsForHash().delete("refresh_token", username);
    }

    // 리프레시 엔티티 저장 (추가 기능)
    public void save(RefreshToken refreshToken) {
        redisTemplate.opsForHash().put("refresh_token", refreshToken.getUsername(), refreshToken);
    }

}
