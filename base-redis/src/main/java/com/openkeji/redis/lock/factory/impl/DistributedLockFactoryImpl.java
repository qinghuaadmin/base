package com.openkeji.redis.lock.factory.impl;

import com.openkeji.redis.lock.factory.DistributedLockFactory;
import com.openkeji.redis.lock.model.RedissonLockWrapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.LockOptions;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

/**
 * @description: 锁工厂：提供各种锁实例
 * @author: houqinghua
 * @create: 2021-07-16
 */
@Slf4j
public class DistributedLockFactoryImpl implements DistributedLockFactory {

    private final RedissonClient redissonClient;

    public DistributedLockFactoryImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public RedissonLockWrapper getRLock(String lockKey, boolean fair) {
        RLock rLock;
        if (Boolean.TRUE.equals(fair)) {
            rLock = getFairLock(lockKey);
        } else {
            rLock = getLock(lockKey);
        }
        return new RedissonLockWrapper(rLock);
    }

    @Override
    public RLock getLock(String name) {
        return redissonClient.getLock(name);
    }

    @Override
    public RReadWriteLock getReadWriteLock(String name) {
        return redissonClient.getReadWriteLock(name);
    }

    @Override
    public RLock getFairLock(String name) {
        return redissonClient.getFairLock(name);
    }

    @Override
    public RLock getMultiLock(RLock... locks) {
        return redissonClient.getMultiLock(locks);
    }

    @Override
    public RLock getSpinLock(String name) {
        return redissonClient.getSpinLock(name);
    }

    @Override
    public RLock getSpinLock(String name, LockOptions.BackOff backOff) {
        return redissonClient.getSpinLock(name, backOff);
    }
}
