package com.openkeji.redis.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @program: base
 * @description: 缓存抽象类
 * @author: houqh
 * @create: 2023-09-19
 */
@SuppressWarnings("all")
@RequiredArgsConstructor
public abstract class AbstractCacheManager {

    private final RedisTemplate redisTemplate;

}
