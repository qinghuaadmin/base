package com.openkeji.redis;

import com.openkeji.redis.manager.AbstractValueCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
@Component
@SuppressWarnings("all")
public class TestCacheManager extends AbstractValueCacheManager<String, String> {

    @Autowired
    private TestCacheManager testCacheManager;

    @PostConstruct
    public void init() {
        expireTime = 10;
        expireTimeUnit = TimeUnit.DAYS;
        createObjectWriteLock = false;
    }

    @Override
    public String getCacheNamePrefix() {
        return null;
    }

    public void test(){
    }
}
