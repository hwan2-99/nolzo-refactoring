package com.noljo.nolzo.auth.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(Long memberId, String refreshToken) {
        String key = generateKey(memberId);
        Duration expiration = Duration.ofSeconds(refreshTokenValidityInSeconds);
        redisTemplate.opsForValue().set(key, refreshToken, expiration);
    }

    @Override
    public String findByMemberId(Long memberId) {
        String key = generateKey(memberId);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        String key = generateKey(memberId);
        redisTemplate.delete(key);
    }

    private String generateKey(Long memberId) {
        return REFRESH_TOKEN_PREFIX + memberId;
    }
}