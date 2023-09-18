package com.openkeji.redis;

import cn.sino.msg.notice.center.common.redis.config.SinoRedisTemplateConfiguration;
import cn.sino.msg.notice.center.common.redis.maker.LettuceConnectionFactoryMaker;
import cn.sino.msg.notice.center.common.redis.properties.RedisLettuceClusterProperties;
import cn.sino.msg.notice.center.common.redis.properties.RedisLettucePoolProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * redis集群配置
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
@ConditionalOnMissingBean(SinoRedisClusterConfiguration.class)
public class SinoRedisClusterConfiguration extends SinoRedisTemplateConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.redis.cluster")
    public RedisLettuceClusterProperties redisLettuceClusterProperties() {
        return new RedisLettuceClusterProperties();
    }

    @Bean("redisConnectionFactory")
    public LettuceConnectionFactory lettuceConnectionFactory(RedisLettucePoolProperties redisLettucePoolProperties,
                                                             RedisLettuceClusterProperties redisLettuceClusterProperties) {
        return LettuceConnectionFactoryMaker.makeClusterConnection(redisLettucePoolProperties, redisLettuceClusterProperties);
    }

}
