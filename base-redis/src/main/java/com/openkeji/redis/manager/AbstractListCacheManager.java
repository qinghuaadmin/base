package com.openkeji.redis.manager;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.core.BoundListOperations;

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
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        boundKeyOperations.trim(start, end);
    }

    public Long size(PK id) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.size();
    }

    public Long leftPush(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPush(value);
    }

    public Long leftPushAll(PK id, V... values) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPushAll(values);
    }

    public Long leftPushIfPresent(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPushIfPresent(value);
    }

    public Long leftPush(PK id, V pivot, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPush(pivot, value);
    }

    public Long rightPush(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPush(value);
    }

    public Long rightPushAll(PK id, V... values) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPushAll(values);
    }

    public Long rightPushIfPresent(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPushIfPresent(value);
    }

    public Long rightPush(PK id, V pivot, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPush(pivot, value);
    }

    public V move(PK id, RedisListCommands.Direction from, PK destinationKey, RedisListCommands.Direction to) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        final String fullDestinationKey = makeFullCacheKey(destinationKey);
        return boundKeyOperations.move(from, (PK) fullDestinationKey, to);
    }

    public V move(PK id, RedisListCommands.Direction from, PK destinationKey, RedisListCommands.Direction to, Duration timeout) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        final String fullDestinationKey = makeFullCacheKey(destinationKey);
        return boundKeyOperations.move(from, (PK) fullDestinationKey, to, timeout);
    }

    public V move(PK id, RedisListCommands.Direction from, PK destinationKey, RedisListCommands.Direction to, long timeout, TimeUnit unit) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        final String fullDestinationKey = makeFullCacheKey(destinationKey);
        return boundKeyOperations.move(from, (PK) fullDestinationKey, to, timeout, unit);
    }

    public void set(PK id, long index, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        boundKeyOperations.set(index, value);
    }

    public Long remove(PK id, long count, Object value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.remove(count, value);
    }

    public V index(PK id, long index) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.index(index);
    }

    public Long indexOf(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.indexOf(value);
    }

    public Long lastIndexOf(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.lastIndexOf(value);
    }

    public V leftPop(PK id) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPop();
    }

    public List<V> leftPop(PK id, long count) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPop(count);
    }

    public V leftPop(PK id, long timeout, TimeUnit unit) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPop(timeout, unit);
    }

    public V rightPop(PK id) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPop();
    }

    public List<V> rightPop(PK id, long count) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPop(count);
    }

    public V rightPop(PK id, long timeout, TimeUnit unit) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPop(timeout, unit);
    }

    public V leftPop(PK id, Duration timeout) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPop(timeout);
    }

    public V rightPop(PK id, Duration timeout) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPop(timeout);
    }
}
