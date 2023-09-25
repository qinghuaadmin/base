package com.openkeji.redis.manager;

import com.openkeji.normal.enums.redis.AbstractCacheNamePrefix;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundKeyOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

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
     * 更新过期时间
     */
    final protected void expire(PK id) {
        getBoundKeyOperations(id)
                .expire(expireTime, expireTimeUnit);
    }

    /**
     * 更新过期时间
     *
     * @param id
     * @param expireTime
     * @param expireTimeUnit
     */
    final protected void expire(PK id, long expireTime, TimeUnit expireTimeUnit) {
        getBoundKeyOperations(id)
                .expire(expireTime, expireTimeUnit);
    }

    /**
     * 删除
     */
    final protected void remove(PK id) {
        final String fullCacheKey = makeFullCacheKey(id);
        redisTemplate.delete(fullCacheKey);
    }

    /**
     * 包含key
     */
    final protected boolean hasKey(PK id) {
        final String fullCacheKey = makeFullCacheKey(id);
        return Boolean.TRUE.equals(redisTemplate.hasKey(fullCacheKey));
    }

    /**
     * 获取缓存key前缀
     */
    final protected String getCacheKeyPrefix() {
        return getCacheNamePrefix();
    }

    /**
     * 拼接完整key
     */
    final protected String makeFullCacheKey(PK id) {
        final String prefix = this.getCacheKeyPrefix();
        return prefix + id;
    }
}
