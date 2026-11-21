package com.noljo.nolzo.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisConnectionDetails connectionDetails) {
        RedisConnectionDetails.Standalone standalone = connectionDetails.getStandalone();
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(standalone.getHost());
        redisStandaloneConfiguration.setPort(standalone.getPort());
        redisStandaloneConfiguration.setDatabase(standalone.getDatabase());
        if (connectionDetails.getUsername() != null && !connectionDetails.getUsername().isBlank()) {
            redisStandaloneConfiguration.setUsername(connectionDetails.getUsername());
        }
        if (connectionDetails.getPassword() != null && !connectionDetails.getPassword().isBlank()) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(connectionDetails.getPassword()));
        }
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public RedissonClient redissonClient(RedisConnectionDetails connectionDetails) {
        RedisConnectionDetails.Standalone standalone = connectionDetails.getStandalone();
        Config config = new Config();
        var singleServerConfig = config.useSingleServer()
                .setAddress("redis://" + standalone.getHost() + ":" + standalone.getPort())
                .setDatabase(standalone.getDatabase());

        if (connectionDetails.getUsername() != null && !connectionDetails.getUsername().isBlank()) {
            singleServerConfig.setUsername(connectionDetails.getUsername());
        }
        if (connectionDetails.getPassword() != null && !connectionDetails.getPassword().isBlank()) {
            singleServerConfig.setPassword(connectionDetails.getPassword());
        }

        return Redisson.create(config);
    }
}
