package com.noljo.nolzo.support;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class RedisCleanerExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        ApplicationContext applicationContext =
                SpringExtension.getApplicationContext(context);

        StringRedisTemplate redisTemplate =
                applicationContext.getBean(StringRedisTemplate.class);

        redisTemplate.getConnectionFactory()
                .getConnection()
                .flushDb();
    }
}
