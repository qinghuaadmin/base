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

    protected List<V> range(PK id, long start, long end) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.range(start, end);
    }

    protected void trim(PK id, long start, long end) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        boundKeyOperations.trim(start, end);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }
    }

    protected Long size(PK id) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.size();
    }

    protected Long leftPush(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        final Long leftPush = boundKeyOperations.leftPush(value);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }
        return leftPush;
    }

    protected Long leftPushAll(PK id, V... values) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        final Long leftPushAll = boundKeyOperations.leftPushAll(values);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }
        return leftPushAll;
    }

    protected Long leftPushIfPresent(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPushIfPresent(value);
    }

    protected Long leftPush(PK id, V pivot, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPush(pivot, value);
    }

    protected Long rightPush(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPush(value);
    }

    protected Long rightPushAll(PK id, V... values) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPushAll(values);
    }

    protected Long rightPushIfPresent(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPushIfPresent(value);
    }

    protected Long rightPush(PK id, V pivot, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPush(pivot, value);
    }

    protected V move(PK id, RedisListCommands.Direction from, PK destinationKey, RedisListCommands.Direction to) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        final String fullDestinationKey = makeFullCacheKey(destinationKey);
        return boundKeyOperations.move(from, (PK) fullDestinationKey, to);
    }

    protected V move(PK id, RedisListCommands.Direction from, PK destinationKey, RedisListCommands.Direction to, Duration timeout) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        final String fullDestinationKey = makeFullCacheKey(destinationKey);
        return boundKeyOperations.move(from, (PK) fullDestinationKey, to, timeout);
    }

    protected V move(PK id, RedisListCommands.Direction from, PK destinationKey, RedisListCommands.Direction to, long timeout, TimeUnit unit) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        final String fullDestinationKey = makeFullCacheKey(destinationKey);
        return boundKeyOperations.move(from, (PK) fullDestinationKey, to, timeout, unit);
    }

    protected void set(PK id, long index, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        boundKeyOperations.set(index, value);
    }

    protected Long remove(PK id, long count, Object value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.remove(count, value);
    }

    protected V index(PK id, long index) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.index(index);
    }

    protected Long indexOf(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.indexOf(value);
    }

    protected Long lastIndexOf(PK id, V value) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.lastIndexOf(value);
    }

    protected V leftPop(PK id) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPop();
    }

    protected List<V> leftPop(PK id, long count) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPop(count);
    }

    protected V leftPop(PK id, long timeout, TimeUnit unit) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPop(timeout, unit);
    }

    protected V rightPop(PK id) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPop();
    }

    protected List<V> rightPop(PK id, long count) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPop(count);
    }

    protected V rightPop(PK id, long timeout, TimeUnit unit) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPop(timeout, unit);
    }

    protected V leftPop(PK id, Duration timeout) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.leftPop(timeout);
    }

    protected V rightPop(PK id, Duration timeout) {
        final BoundListOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.rightPop(timeout);
    }
}
