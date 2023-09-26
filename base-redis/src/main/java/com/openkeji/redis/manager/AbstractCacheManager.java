package com.openkeji.redis.manager;

import com.openkeji.normal.enums.redis.AbstractCacheNamePrefix;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundKeyOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: base
 * @description: 缓存抽象类
 * @author: kyle.hou
 * @create: 2023-09-19
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@RequiredArgsConstructor
public abstract class AbstractCacheManager<PK extends Serializable> implements AbstractCacheNamePrefix {

    @Autowired
    protected RedisTemplate redisTemplate;

    /**
     * 缓存类型
     */
    @Getter
    private final DataType dataType;

    /**
     * 默认过期时间 5分钟
     */
    @Getter
    @Setter
    protected int expireTime = 5;

    /**
     * 默认过期时间单位
     */
    @Getter
    @Setter
    protected TimeUnit expireTimeUnit = TimeUnit.MINUTES;

    /**
     * 获取BoundKeyOperations
     */
    protected abstract BoundKeyOperations getBoundKeyOperations(PK id);

    /**
     * 更新时，自动续期
     * 注意：不是所有缓存更新操作都有覆盖
     */
    protected boolean updateExpireTimeWhenUpdate() {
        return Boolean.FALSE;
    }

    protected Boolean expire(PK id) {
        final BoundKeyOperations boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
    }

    protected Boolean expire(PK id, Duration timeout) {
        if (TimeoutUtils.hasMillis(timeout)) {
            return expire(id, timeout.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            return expire(id, timeout.getSeconds(), TimeUnit.SECONDS);
        }
    }

    protected Boolean expire(PK id, long timeout, TimeUnit timeUnit) {
        final BoundKeyOperations boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.expire(timeout, timeUnit);
    }

    protected Boolean expireAt(PK id, Instant expireAt) {
        return expireAt(id, Date.from(expireAt));
    }

    protected Boolean expireAt(PK id, Date date) {
        final BoundKeyOperations boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.expireAt(date);
    }

    protected Boolean persist(PK id) {
        final BoundKeyOperations boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.persist();
    }

    final protected void delete(PK id) {
        final String fullCacheKey = makeFullCacheKey(id);
        redisTemplate.delete(fullCacheKey);
    }

    final protected boolean hasKey(PK id) {
        final String fullCacheKey = makeFullCacheKey(id);
        return Boolean.TRUE.equals(redisTemplate.hasKey(fullCacheKey));
    }

    final protected String makeFullCacheKey(PK id) {
        final String prefix = getCacheNamePrefix();
        return prefix + id;
    }

    final protected List<String> makeFullCacheKey(Collection<PK> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(this::makeFullCacheKey)
                .collect(Collectors.toList());
    }
}
