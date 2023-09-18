package com.openkeji.redis.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisLettucePoolProperties {
    /*
        最小空闲连接数
     */
    private Integer minIdle;
    /*
        最大空闲连接数 大于0保证连接是可以服用的
     */
    private Integer maxIdle;
    /*
        最大连接数
     */
    private Integer maxTotal;
    /*
        获取连接超时时间(单位：毫秒) 默认无超时时间
     */
    private Integer timeOut;
    /*
        执行超时时间(单位：秒) 默认3S
     */
    private Integer commandTimeout;
}
