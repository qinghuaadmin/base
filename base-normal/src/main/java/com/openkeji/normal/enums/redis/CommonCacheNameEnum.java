package com.openkeji.normal.enums.redis;

import com.openkeji.normal.enums.BaseStringEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 公共服务缓存声明
 * @author: kyle.hou
 * @create: 2023-09-25-25
 */
@Getter
@AllArgsConstructor
public enum CommonCacheNameEnum implements BaseStringEnum, AbstractKeyPrefix {

    BASE_PENETRATE_PROTECT("base","penetrate:protect",""),
            ;

    /**
     * 业务分组 可以使用ApplicationName
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

    @Override
    public String getValue() {
        return keyGroup;
    }

    @Override
    public String getDesc() {
        return description;
    }
}
