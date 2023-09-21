package com.openkeji.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description: Redis分布式锁声明
 * @author: houqinghua
 * @create: 2021-07-16
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RedisKeyPrefixEnum implements DistributedLockKeyPrefix {

    MESSAGE_CALLBACK("sino-msg-notice-center", "message:callback", "sino-message回调"),
    ;

    /**
     * 服务名称 ApplicationName
     */
    private String keyGroup;
    /**
     * 缓存key
     */
    private String key;
    /**
     * 描述
     */
    private String description;

    @Override
    public String getDistributedLockKeyPrefix() {
        return keyGroup + ":" + key + ":";
    }
}
