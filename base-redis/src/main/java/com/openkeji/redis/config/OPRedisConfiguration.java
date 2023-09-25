package com.openkeji.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openkeji.redis.maker.RedisTemplateMaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;


/**
 * @program: sino-msg-notice-center
 * @description: Redis配置类 兼容单机/集群模式
 * @author: houqh
 * @create: 2023-09-18
 */
@Slf4j
@RefreshScope
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisOperations.class)
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
@Import({LettuceConnectionConfiguration.class})
public class OPRedisConfiguration {

    @PostConstruct
    public void init() {
        log.info("[OPRedisConfiguration.init] init successful");
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return RedisTemplateMaker.makeObjectMapper();
    }


    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        log.info("[OPRedisConfiguration.redisTemplate] init successful");
        return RedisTemplateMaker.makeRedisTemplate(redisConnectionFactory, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stringRedisTemplate")
    @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("[OPRedisConfiguration.stringRedisTemplate] init successful");
        return RedisTemplateMaker.makeStringRedisTemplate(redisConnectionFactory);
    }
}
