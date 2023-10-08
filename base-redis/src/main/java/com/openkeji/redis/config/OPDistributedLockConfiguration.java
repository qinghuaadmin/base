package com.openkeji.redis.config;

import com.openkeji.redis.lock.DistributedLockTemplate;
import com.openkeji.redis.lock.factory.DistributedLockFactory;
import com.openkeji.redis.lock.factory.impl.DistributedLockFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-19
 */
@Slf4j
@RefreshScope
@Configuration
@Import({OPRedissonConfiguration.class})
public class OPDistributedLockConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DistributedLockFactory distributedLockFactory(RedissonClient redissonClient) {
        log.info("[OPDistributedLockConfiguration.distributedLockFactory] init");
        return new DistributedLockFactoryImpl(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public DistributedLockTemplate distributedLockTemplate(DistributedLockFactory distributedLockFactory) {
        log.info("[OPDistributedLockConfiguration.distributedLockTemplate] init");
        return new DistributedLockTemplate(distributedLockFactory);
    }
}
