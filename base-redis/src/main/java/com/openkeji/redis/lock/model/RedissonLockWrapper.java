package com.openkeji.redis.lock.model;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedissonLockWrapper implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(RedissonLockWrapper.class);
    private boolean locked = false;
    private final RLock rLock;

    public RedissonLockWrapper(RLock rLock) {
        this.rLock = rLock;
    }

    public boolean tryAcquire(long time, TimeUnit unit) {
        try {
            locked = rLock.tryLock(time, unit);
        } catch (InterruptedException e) {
            log.info("[RedissonLockWrapper.tryAcquire] exception：", e);
            Thread.currentThread().interrupt();
        }
        return locked;
    }

    @Override
    public void close() throws IOException {
        if (locked) {
            try {
                rLock.unlock();
                log.info("[RedissonLockWrapper.close] close successful");
            } catch (Exception e) {
                log.info("[RedissonLockWrapper.close] close error：", e);
            }
        }
    }
}
