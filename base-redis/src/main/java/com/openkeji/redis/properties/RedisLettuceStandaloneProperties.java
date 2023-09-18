package com.openkeji.redis.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: sino-msg-notice-center
 * @description: 单机/proxy配置
 * @author: houqh
 * @create: 2023-07-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisLettuceStandaloneProperties {

    private String host;

    private Integer port;

    private Integer database;

    private String password;
}
