package com.openkeji.redis.manager;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisOperations;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: base
 * @description:
 * @author: kyle.hou
 * @create: 2023-09-25-23
 */
@SuppressWarnings("unchecked")
public abstract class AbstractListCacheManager<PK extends Serializable, V> extends AbstractCacheManager<PK> {

    public AbstractListCacheManager(DataType dataType) {
        super(DataType.LIST);
    }

    /**
     * 获取Operations
     */
    protected BoundListOperations<PK, V> getBoundKeyOperations(PK id) {
        final String fullCacheKey = this.makeFullCacheKey(id);
        return redisTemplate.boundListOps(fullCacheKey);
    }

    public List<V> range(PK id, long start, long end) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.range(start, end);
    }

    public void trim(PK id, long start, long end) {

    }

    public Long size(PK id) {
        return null;
    }

    public Long leftPush(PK id, V value) {
        return null;
    }

    public Long leftPushAll(PK id, V... values) {
        return null;
    }

    public Long leftPushIfPresent(PK id, V value) {
        return null;
    }

    public Long leftPush(PK id, V pivot, V value) {
        return null;
    }

    public Long rightPush(PK id, V value) {
        return null;
    }

    public Long rightPushAll(PK id, V... values) {
        return null;
    }

    public Long rightPushIfPresent(PK id, V value) {
        return null;
    }

    public Long rightPush(PK id, V pivot, V value) {
        return null;
    }

    public V move(PK id, RedisListCommands.Direction from, PK destinationKey, RedisListCommands.Direction to) {
        return null;
    }

    public V move(PK id, RedisListCommands.Direction from, PK destinationKey, RedisListCommands.Direction to, Duration timeout) {
        return null;
    }

    public V move(PK id, RedisListCommands.Direction from, PK destinationKey, RedisListCommands.Direction to, long timeout, TimeUnit unit) {
        return null;
    }

    public void set(PK id, long index, V value) {

    }

    public Long remove(PK id, long count, Object value) {
        return null;
    }

    public V index(PK id, long index) {
        return null;
    }

    public Long indexOf(PK id, V value) {
        return null;
    }

    public Long lastIndexOf(PK id, V value) {
        return null;
    }

    public V leftPop(PK id,) {
        return null;
    }

    public List<V> leftPop(PK id, long count) {
        return null;
    }

    public V leftPop(PK id, long timeout, TimeUnit unit) {
        return null;
    }

    public V rightPop(PK id) {
        return null;
    }

    public List<V> rightPop(PK id, long count) {
        return null;
    }

    public V rightPop(PK id, long timeout, TimeUnit unit) {
        return null;
    }

    public V leftPop(PK id, Duration timeout) {
        return BoundListOperations.super.leftPop(timeout);
    }

    public V rightPop(PK id, Duration timeout) {
        return BoundListOperations.super.rightPop(timeout);
    }
}
