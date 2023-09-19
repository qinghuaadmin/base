package com.openkeji.redis.lock.factory;


import com.openkeji.redis.lock.model.RedissonLockWrapper;

public interface DistributedLockFactory {
    /**
     * 获取redis分布式锁实例
     *
     * @param lockKey
     * @return
     */
    RedissonLockWrapper getRLock(String lockKey);
}
