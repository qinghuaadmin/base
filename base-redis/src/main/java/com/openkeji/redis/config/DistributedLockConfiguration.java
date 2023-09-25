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

import javax.annotation.PostConstruct;

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
public class DistributedLockConfiguration {

    @PostConstruct
    public void init() {
        log.info("[DistributedLockConfiguration.init] init successful");
    }

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
