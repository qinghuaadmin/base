package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;

/**
 * @program: base
 * @description: OPRedisConnectionConfiguration
 * @author: kyle.hou
 * @create: 2023-09-20-01
 */
public class OPRedisConnectionConfiguration extends RedisConnectionConfiguration {

    protected OPRedisConnectionConfiguration(RedisProperties properties,
                                             ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider,
                                             ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                             ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        super(properties, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider);
    }
}
