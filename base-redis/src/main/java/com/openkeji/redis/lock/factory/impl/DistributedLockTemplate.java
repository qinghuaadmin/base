package com.openkeji.redis.lock.factory.impl;

import cn.sino.msg.notice.center.common.redis.lock.enums.DistributedLockKeyPrefix;
import cn.sino.msg.notice.center.common.redis.lock.factory.DistributedLockFactory;
import cn.sino.msg.notice.center.common.redis.lock.model.RedissonLockWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * @description: 分布式锁实现
 * @author: houqinghua
 * @create: 2021-07-16
 */

@SuppressWarnings("java:S112")
public class DistributedLockTemplate {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLockTemplate.class);

    /**
     * 默认获取锁超时时间
     */
    public static final int DEFAULT_ACQUIRE_TIME = 60 * 3;

    private final DistributedLockFactory distributedLockFactory;

    public DistributedLockTemplate(DistributedLockFactory distributedLockFactory) {
        this.distributedLockFactory = distributedLockFactory;
    }

    /**
     * Redis分布式锁,基于Redis实现
     */
    public <T> T executeWithRLock(DistributedLockKeyPrefix lockKeyPrefix,
                                  String key,
                                  BusinessOperation<T> operation) {
        return this.executeWithRLock(lockKeyPrefix, key, DEFAULT_ACQUIRE_TIME, TimeUnit.SECONDS, operation);
    }

    /**
     * Redis分布式锁,基于Redis实现
     * 支持自定义获取锁时间
     */
    public <T> T executeWithRLock(DistributedLockKeyPrefix lockKeyPrefix,
                                  String key,
                                  long time,
                                  TimeUnit unit,
                                  BusinessOperation<T> operation) {
        key = lockKeyPrefix.getDistributedLockKeyPrefix() + key;
        final String threadName = Thread.currentThread().getName();
        try (RedissonLockWrapper rLock = distributedLockFactory.getRLock(key)) {
            final boolean tryAcquire = rLock.tryAcquire(time, unit);
            if (!tryAcquire) {
                throw new RuntimeException(MessageFormat.format("{0} 获取分布式锁失败", threadName));
            }
            logger.debug("{} 获取分布式锁成功", threadName);
            return operation.execute();
        } catch (Exception e) {
            logger.warn("获取分布式锁失败异常", e);
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface BusinessOperation<T> {
        T execute();
    }
}
