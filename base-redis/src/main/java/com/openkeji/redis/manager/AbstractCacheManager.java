package com.openkeji.redis.manager;

import lombok.RequiredArgsConstructor;
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
public abstract class AbstractCacheManager<PK extends Serializable> implements AbstractRedisKeyPrefix {
    @Autowired
    protected RedisTemplate redisTemplate;

    /**
     * 缓存类型
     */
    private final DataType dataType;

    /**
     * 默认过期时间 10分钟
     */
    protected int expireTime = 10;
    protected TimeUnit expireTimeUnit = TimeUnit.MINUTES;

    /**
     * 获取OPS
     */
    protected abstract BoundKeyOperations getOperations(PK id);

    /**
     * 更新过期时间
     */
    final protected void expire(PK id) {
        final String fullCacheKey = makeFullCacheKey(id);
        redisTemplate.expire(fullCacheKey, expireTime, expireTimeUnit);
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
        return getKeyPrefix();
    }

    /**
     * 拼接完整key
     */
    final protected String makeFullCacheKey(PK id) {
        final String cacheKeyPrefix = this.getCacheKeyPrefix();
        return cacheKeyPrefix + id;
    }
}
