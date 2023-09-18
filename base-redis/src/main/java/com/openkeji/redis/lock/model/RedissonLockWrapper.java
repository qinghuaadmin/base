package com.openkeji.redis.lock.model;

import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: houqinghua
 * @create: 2021-07-16
 */
@SuppressWarnings("java:S2142")
public class RedissonLockWrapper implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(RedissonLockWrapper.class);
    private boolean locked = Boolean.FALSE;
    private final RLock rLock;

    public RedissonLockWrapper(RLock rLock) {
        this.rLock = rLock;
    }

    public boolean tryAcquire(long time, TimeUnit unit) {
        try {
            locked = rLock.tryLock(time, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return locked;
    }

    @Override
    public void close() throws IOException {
        if (locked) {
            try {
                rLock.unlock();
            } catch (Exception e) {
                logger.warn("release lock error", e);
            }
        }
    }
}
