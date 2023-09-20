package com.openkeji.redis;

import com.openkeji.redis.manager.AbstractCacheManager;
import org.springframework.stereotype.Component;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
@Component
@SuppressWarnings("all")
public class TestCacheManager extends AbstractCacheManager {


    @Override
    public String getRedisKeyPrefix() {
        return null;
    }

}
