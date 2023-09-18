package com.openkeji.redis;

import cn.sino.msg.notice.center.common.redis.config.SinoDistributedLockConfiguration;
import cn.sino.msg.notice.center.common.redis.maker.RedissonConnectionFactoryMaker;
import cn.sino.msg.notice.center.common.redis.properties.RedisLettuceClusterProperties;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: distributed-lock
 * @description: redisson集群配置
 * @author: kyle.hou
 * @create: 2021-06-30
 */
@Configuration
@Import(SinoRedisClusterConfiguration.class)
public class SinoDistributedLockClusterConfiguration extends SinoDistributedLockConfiguration {

    @Bean
    @ConditionalOnBean(SinoRedisClusterConfiguration.class)
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient(RedisLettuceClusterProperties redisProperties) {
        return RedissonConnectionFactoryMaker.makeClusterServersConfigFactory(redisProperties);
    }
}
