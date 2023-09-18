package com.openkeji.redis;

import cn.sino.msg.notice.center.common.redis.config.SinoRedisTemplateConfiguration;
import cn.sino.msg.notice.center.common.redis.maker.LettuceConnectionFactoryMaker;
import cn.sino.msg.notice.center.common.redis.properties.RedisLettucePoolProperties;
import cn.sino.msg.notice.center.common.redis.properties.RedisLettuceStandaloneProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * redis单机配置
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
@ConditionalOnMissingBean(SinoRedisStandaloneConfiguration.class)
public class SinoRedisStandaloneConfiguration extends SinoRedisTemplateConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisLettuceStandaloneProperties redisLettuceStandaloneProperties() {
        return new RedisLettuceStandaloneProperties();
    }

    @Bean("redisConnectionFactory")
    public LettuceConnectionFactory lettuceConnectionFactory(RedisLettucePoolProperties redisLettucePoolProperties,
                                                             RedisLettuceStandaloneProperties redisLettuceStandaloneProperties) {
        return LettuceConnectionFactoryMaker.makeStandaloneConnection(redisLettucePoolProperties, redisLettuceStandaloneProperties);
    }

}

