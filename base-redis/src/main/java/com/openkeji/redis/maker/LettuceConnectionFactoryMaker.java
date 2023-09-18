package com.openkeji.redis.maker;

import com.openkeji.redis.properties.RedisLettuceClusterProperties;
import com.openkeji.redis.properties.RedisLettucePoolProperties;
import com.openkeji.redis.properties.RedisLettuceStandaloneProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: sino-msg-notice-center
 * @description:
 * @author: houqh
 * @create: 2023-07-28
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LettuceConnectionFactoryMaker {

    public static final int DEFAULT_DATABASE = 0;

    public static final int DEFAULT_COMMAND_TIMEOUT = 3;

    public static final int DEFAULT_CLUSTER_MAX_REDIRECTS = 5;

    public static LettuceConnectionFactory makeStandaloneConnection(RedisLettucePoolProperties redisPoolProperties,
                                                                    RedisLettuceStandaloneProperties redisProperties) {

        // connect config
        RedisStandaloneConfiguration connectConfig = new RedisStandaloneConfiguration();
        connectConfig.setHostName(redisProperties.getHost());
        connectConfig.setPort(redisProperties.getPort());
        connectConfig.setPassword(redisProperties.getPassword());
        connectConfig.setDatabase(
                Optional.ofNullable(redisProperties.getDatabase()).orElse(DEFAULT_DATABASE)
        );

        return buildLettuceConnectionFactory(connectConfig, redisPoolProperties);
    }

    public static LettuceConnectionFactory makeClusterConnection(RedisLettucePoolProperties redisPoolProperties,
                                                                 RedisLettuceClusterProperties redisProperties) {
        Assert.isTrue(CollectionUtils.isNotEmpty(redisProperties.getNodes()), "redis config illegal argument nodes");

        final Set<RedisNode> redisNodeSet = redisProperties.getNodes().stream().map(node -> {
            final String[] hostAndPorts = StringUtils.split(node, ":");
            return new RedisNode(hostAndPorts[0], Integer.parseInt(hostAndPorts[1]));
        }).collect(Collectors.toSet());

        /*
            集群模式下，仅有一个节点时初始化为单机模式
         */
        if (redisNodeSet.size() == 1) {
            final RedisNode redisNode = redisNodeSet.iterator().next();
            RedisStandaloneConfiguration connectConfig = new RedisStandaloneConfiguration();

            Assert.notNull(redisNode.getHost(), "illegal argument redis node host");
            Assert.notNull(redisNode.getPort(), "illegal argument redis node port");
            connectConfig.setHostName(redisNode.getHost());
            connectConfig.setPort(redisNode.getPort());
            connectConfig.setPassword(redisProperties.getPassword());
            connectConfig.setDatabase(
                    Optional.ofNullable(redisProperties.getDatabase()).orElse(DEFAULT_DATABASE)
            );
            return buildLettuceConnectionFactory(connectConfig, redisPoolProperties);
        } else {
            final RedisClusterConfiguration connectConfig = new RedisClusterConfiguration();
            connectConfig.setClusterNodes(redisNodeSet);
            connectConfig.setMaxRedirects(
                    Optional.ofNullable(redisProperties.getMaxRedirects()).orElse(DEFAULT_CLUSTER_MAX_REDIRECTS)
            );
            connectConfig.setPassword(redisProperties.getPassword());
            return buildLettuceConnectionFactory(connectConfig, redisPoolProperties);
        }
    }

    @SuppressWarnings("rawtypes")
    private static LettuceConnectionFactory buildLettuceConnectionFactory(RedisConfiguration redisConfiguration, RedisLettucePoolProperties redisPoolProperties) {

        // pool config
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(Optional.ofNullable(redisPoolProperties.getMaxIdle())
                .orElse(GenericObjectPoolConfig.DEFAULT_MAX_IDLE)
        );
        poolConfig.setMaxTotal(Optional.ofNullable(redisPoolProperties.getMaxTotal())
                .orElse(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL)
        );
        poolConfig.setMinIdle(Optional.ofNullable(redisPoolProperties.getMinIdle())
                .orElse(GenericObjectPoolConfig.DEFAULT_MIN_IDLE)
        );
        poolConfig.setMaxWait(
                redisPoolProperties.getTimeOut() == null ? BaseObjectPoolConfig.DEFAULT_MAX_WAIT : Duration.ofMillis(redisPoolProperties.getTimeOut())
        );
        poolConfig.setTestOnBorrow(Boolean.TRUE);

        // lettuce pool
        final LettucePoolingClientConfiguration poolingClientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .commandTimeout(
                        redisPoolProperties.getCommandTimeout() == null ? Duration.ofSeconds(DEFAULT_COMMAND_TIMEOUT)
                                : Duration.ofSeconds(redisPoolProperties.getCommandTimeout())
                )
                .build();

        // connectionFactory
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfiguration, poolingClientConfig);
        connectionFactory.afterPropertiesSet();

        return connectionFactory;
    }
}
