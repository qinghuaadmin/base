package com.openkeji.redis.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: sino-msg-notice-center
 * @description: 集群配置
 * @author: houqh
 * @create: 2023-07-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisLettuceClusterProperties {

    private List<String> nodes;

    /**
     * 最大重定向次数
     */
    private Integer maxRedirects;

    /**
     * 仅有一个节点时,启用此配置
     */
    private Integer database;

    private String password;
}
