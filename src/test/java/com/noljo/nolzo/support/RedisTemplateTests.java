package com.noljo.nolzo.support;

import com.noljo.nolzo.support.annotation.ServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

@ServiceTest
@ActiveProfiles("test")
class RedisTemplateTests {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("Redis 동작 테스트")
    void redisTemplateString() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String key = "name";
        valueOperations.set(key, "NOLZO");
        String value = valueOperations.get(key);
        Assertions.assertEquals(value, "NOLZO");
    }
}
