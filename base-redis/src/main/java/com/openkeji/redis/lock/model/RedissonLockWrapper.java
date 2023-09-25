package com.openkeji.redis.lock.model;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedissonLockWrapper implements AutoCloseable {
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
    public void close() {
        if (locked) {
            try {
                rLock.unlock();
                log.debug("[RedissonLockWrapper.close] close successful");
            } catch (Exception e) {
                log.info("[RedissonLockWrapper.close] close error：", e);
            }
        }
    }
}
