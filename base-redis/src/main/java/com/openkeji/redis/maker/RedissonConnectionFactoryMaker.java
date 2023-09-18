package com.openkeji.redis.maker;

import com.openkeji.redis.properties.RedisLettuceClusterProperties;
import com.openkeji.redis.properties.RedisLettuceStandaloneProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * @program: sino-msg-notice-center
 * @description:
 * @author: houqh
 * @create: 2023-07-28
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedissonConnectionFactoryMaker {

    public static final int DEFAULT_DATABASE = 0;

    public static final String REDIS_PROTOCOL_PREFIX = "redis://";

    public static RedissonClient makeSingleServerConfigFactory(RedisLettuceStandaloneProperties redisProperties) {
        final Config config = new Config();
        final SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(
                REDIS_PROTOCOL_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort()
        );
        singleServerConfig.setPassword(redisProperties.getPassword());
        singleServerConfig.setDatabase(
                Optional.ofNullable(redisProperties.getDatabase()).orElse(DEFAULT_DATABASE)
        );
        return Redisson.create(config);
    }

    public static RedissonClient makeClusterServersConfigFactory(RedisLettuceClusterProperties redisProperties) {
        final List<String> nodes = redisProperties.getNodes();
        Assert.isTrue(CollectionUtils.isNotEmpty(redisProperties.getNodes()), "redisson config illegal argument nodes");

        /*
            集群模式下，仅有一个节点时初始化为单机模式
         */
        final Config config = new Config();
        if (nodes.size() == 1) {
            final SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setAddress(
                    REDIS_PROTOCOL_PREFIX + nodes.get(0)
            );
            singleServerConfig.setPassword(redisProperties.getPassword());
            singleServerConfig.setDatabase(
                    Optional.ofNullable(redisProperties.getDatabase()).orElse(DEFAULT_DATABASE)
            );
        } else {
            final ClusterServersConfig clusterServersConfig = config.useClusterServers();
            clusterServersConfig.setNodeAddresses(redisProperties.getNodes());
            clusterServersConfig.setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }
}
