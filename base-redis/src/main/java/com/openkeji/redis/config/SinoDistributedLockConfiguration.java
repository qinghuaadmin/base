package com.openkeji.redis.config;


import com.openkeji.redis.lock.factory.DistributedLockFactory;
import com.openkeji.redis.lock.factory.impl.DistributedLockFactoryImpl;
import com.openkeji.redis.lock.factory.impl.DistributedLockTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @program: sino-msg-notice-center
 * @description:
 * @author: houqh
 * @create: 2023-07-31
 */
public class SinoDistributedLockConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DistributedLockFactory distributedLockFactory(RedissonClient redissonClient) {
        return new DistributedLockFactoryImpl(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public DistributedLockTemplate distributedLockTemplate(DistributedLockFactory distributedLockFactory) {
        return new DistributedLockTemplate(distributedLockFactory);
    }
}
