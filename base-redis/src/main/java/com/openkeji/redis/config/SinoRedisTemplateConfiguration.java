package com.openkeji.redis.config;

import com.openkeji.redis.maker.RedisTemplateMaker;
import com.openkeji.redis.properties.RedisLettucePoolProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @program: sino-msg-notice-center
 * @description:
 * @author: houqh
 * @create: 2023-07-31
 */
public class SinoRedisTemplateConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.redis.lettuce.pool")
    public RedisLettucePoolProperties redisLettucePoolProperties() {
        return new RedisLettucePoolProperties();
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return RedisTemplateMaker.makeRedisTemplate(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stringRedisTemplate")
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return RedisTemplateMaker.makeStringRedisTemplate(redisConnectionFactory);
    }
}
