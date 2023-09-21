package com.openkeji.redis.lock;

import com.openkeji.redis.exception.TryAcquireLockTimeOutException;
import com.openkeji.redis.lock.factory.DistributedLockFactory;
import com.openkeji.redis.lock.model.RedissonLockWrapper;
import com.openkeji.redis.manager.AbstractRedisKeyPrefix;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * @description: 分布式锁实现
 * @author: houqinghua
 * @create: 2021-07-16
 */
@Slf4j
public class DistributedLockTemplate {

    /**
     * 默认获取锁超时时间
     */
    public static final int DEFAULT_ACQUIRE_TIME = 10 * 3;
    public static final TimeUnit DEFAULT_ACQUIRE_TIME_UNIT = TimeUnit.SECONDS;

    private final DistributedLockFactory distributedLockFactory;

    public DistributedLockTemplate(DistributedLockFactory distributedLockFactory) {
        this.distributedLockFactory = distributedLockFactory;
    }

    public <T> T executeWithRLock(AbstractRedisKeyPrefix lockKeyPrefix,
                                  String key,
                                  Function<T> function) {
        return this.executeWithRLock(lockKeyPrefix, key, DEFAULT_ACQUIRE_TIME, DEFAULT_ACQUIRE_TIME_UNIT, function);
    }

    public <T> T executeWithRLock(AbstractRedisKeyPrefix lockKeyPrefix, String key, long time, TimeUnit unit, Function<T> function) {
        final String lockKey = MessageFormat.format("{0}:{1}", lockKeyPrefix.getKeyPrefix(), key);
        try (RedissonLockWrapper rLock = distributedLockFactory.getRLock(lockKey)) {
            final boolean tryAcquire = rLock.tryAcquire(time, unit);
            if (!tryAcquire) {
                throw new TryAcquireLockTimeOutException(MessageFormat.format("try acquire lock timeout {0}", lockKey));
            }
            return function.execute();
        } catch (Exception e) {
            log.info("[DistributedLockTemplate.executeWithRLock] function execute error：", e);
            if (e instanceof TryAcquireLockTimeOutException) {
                throw (TryAcquireLockTimeOutException) e;
            }
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface Function<T> {
        T execute();
    }
}
