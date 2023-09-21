package com.openkeji.redis.common;

import com.openkeji.redis.manager.AbstractRedisKeyPrefix;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: Redis分布式锁声明
 * @author: kyle.hou
 * @create: 2023-07-16
 */
@Getter
@AllArgsConstructor
public enum DistributedLockKeyPrefixEnum implements AbstractRedisKeyPrefix {

    CREATEOBJECT_WRITELOCK("base", "createobject:write", "创建缓存写锁"),
    ;

    /**
     * 业务分组 ApplicationName
     */
    private final String keyGroup;
    /**
     * 缓存key名称
     */
    private final String key;
    /**
     * 描述
     */
    private final String description;

    @Override
    public String getKeyPrefix() {
        return keyGroup + ":" + key + ":";
    }
}
