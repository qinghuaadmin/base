package com.openkeji.redis.lock.factory.impl;

import com.openkeji.redis.lock.factory.DistributedLockFactory;
import com.openkeji.redis.lock.model.RedissonLockWrapper;
import org.redisson.api.RedissonClient;

/**
 * @description: 锁工厂：提供各种锁实例
 * @author: houqinghua
 * @create: 2021-07-16
 */
public class DistributedLockFactoryImpl implements DistributedLockFactory {

    private final RedissonClient redissonClient;

    public DistributedLockFactoryImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public RedissonLockWrapper getRLock(String lockKey) {
        return new RedissonLockWrapper(redissonClient.getLock(lockKey));
    }
}
