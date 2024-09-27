package com.acoldbottle.todolist.config;

import com.acoldbottle.todolist.domain.RefreshToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories // RedisRepository 기능을 활성화
public class RedisConfig {

    /**
     * RedisTemplate 설정
     *
     * @param redisConnectionFactory Redis 연결 팩토리
     * @return RedisTemplate<String, RefreshToken>  -> String = "refresh_token"
     */
    @Bean
    public RedisTemplate<String, RefreshToken> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, RefreshToken> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 키 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        // 값 직렬화
        Jackson2JsonRedisSerializer<RefreshToken> serializer = new Jackson2JsonRedisSerializer<>(RefreshToken.class);
        template.setValueSerializer(serializer);

        // 필요 시, 해시 키와 값 직렬화도 설정
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }
}

