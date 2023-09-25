package com.openkeji.redis.lock.factory;

import org.redisson.api.LockOptions;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;

public interface Lock {

    RLock getLock(String name);

    RReadWriteLock getReadWriteLock(String name);

    RLock getFairLock(String name);

    RLock getMultiLock(RLock... locks);

    RLock getSpinLock(String name);

    RLock getSpinLock(String name, LockOptions.BackOff backOff);
}
