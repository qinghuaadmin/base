package com.openkeji.redis.manager;

public interface AbstractRedisKeyPrefix {
    /**
     * 注册key前缀
     *
     * @return
     */
    String getKeyPrefix();
}
