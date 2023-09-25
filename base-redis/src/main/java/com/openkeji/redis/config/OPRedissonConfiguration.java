package com.openkeji.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.redisson.spring.starter.RedissonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-19
 */
@Slf4j
@RefreshScope
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Redisson.class, RedisOperations.class})
@EnableConfigurationProperties({RedissonProperties.class})
@EnableAutoConfiguration(exclude = {RedissonAutoConfiguration.class})
@Import({OPRedisConfiguration.class})
public class OPRedissonConfiguration {

    @PostConstruct
    public void init() {
        log.info("[OPRedissonConfiguration.init] init successful");
    }

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";
    @Autowired(
            required = false
    )
    private List<RedissonAutoConfigurationCustomizer> redissonAutoConfigurationCustomizers;
    @Autowired
    private RedissonProperties redissonProperties;
    @Autowired
    private RedisProperties redisProperties;
    @Autowired
    private ApplicationContext ctx;

    @Bean
    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean({RedissonReactiveClient.class})
    public RedissonReactiveClient redissonReactive(RedissonClient redisson) {
        return redisson.reactive();
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean({RedissonRxClient.class})
    public RedissonRxClient redissonRxJava(RedissonClient redisson) {
        return redisson.rxJava();
    }

    @Bean(
            destroyMethod = "shutdown"
    )
    @ConditionalOnMissingBean({RedissonClient.class})
    public RedissonClient redisson() throws IOException {
        Config config = null;
        Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
        Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
        Object timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, this.redisProperties);
        int timeout;
        Method nodesMethod;
        if (null == timeoutValue) {
            timeout = 10000;
        } else if (!(timeoutValue instanceof Integer)) {
            nodesMethod = ReflectionUtils.findMethod(timeoutValue.getClass(), "toMillis");
            timeout = ((Long) ReflectionUtils.invokeMethod(nodesMethod, timeoutValue)).intValue();
        } else {
            timeout = (Integer) timeoutValue;
        }

        if (this.redissonProperties.getConfig() != null) {
            try {
                config = Config.fromYAML(this.redissonProperties.getConfig());
            } catch (IOException var13) {
                try {
                    config = Config.fromJSON(this.redissonProperties.getConfig());
                } catch (IOException var12) {
                    throw new IllegalArgumentException("Can't parse config", var12);
                }
            }
        } else if (this.redissonProperties.getFile() != null) {
            try {
                InputStream is = this.getConfigStream();
                config = Config.fromYAML(is);
            } catch (IOException var11) {
                try {
                    InputStream is = this.getConfigStream();
                    config = Config.fromJSON(is);
                } catch (IOException var10) {
                    throw new IllegalArgumentException("Can't parse config", var10);
                }
            }
        } else if (this.redisProperties.getSentinel() != null) {
            nodesMethod = ReflectionUtils.findMethod(RedisProperties.Sentinel.class, "getNodes");
            Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, this.redisProperties.getSentinel());
            String[] nodes;
            if (nodesValue instanceof String) {
                nodes = this.convert(Arrays.asList(((String) nodesValue).split(",")));
            } else {
                nodes = this.convert((List) nodesValue);
            }

            config = new Config();
            ((SentinelServersConfig) config.useSentinelServers().setMasterName(this.redisProperties.getSentinel().getMaster()).addSentinelAddress(nodes).setDatabase(this.redisProperties.getDatabase()).setConnectTimeout(timeout)).setPassword(this.redisProperties.getPassword());
        } else {
            Method method;
            if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, this.redisProperties) != null) {
                Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, this.redisProperties);
                method = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
                List<String> nodesObject = (List) ReflectionUtils.invokeMethod(method, clusterObject);
                String[] nodes = this.convert(nodesObject);
                config = new Config();
                ((ClusterServersConfig) config.useClusterServers().addNodeAddress(nodes).setConnectTimeout(timeout)).setPassword(this.redisProperties.getPassword());
            } else {
                config = new Config();
                String prefix = "redis://";
                method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
                if (method != null && (Boolean) ReflectionUtils.invokeMethod(method, this.redisProperties)) {
                    prefix = "rediss://";
                }

                ((SingleServerConfig) config.useSingleServer().setAddress(prefix + this.redisProperties.getHost() + ":" + this.redisProperties.getPort()).setConnectTimeout(timeout)).setDatabase(this.redisProperties.getDatabase()).setPassword(this.redisProperties.getPassword());
            }
        }

        if (this.redissonAutoConfigurationCustomizers != null) {
            Iterator var19 = this.redissonAutoConfigurationCustomizers.iterator();

            while (var19.hasNext()) {
                RedissonAutoConfigurationCustomizer customizer = (RedissonAutoConfigurationCustomizer) var19.next();
                customizer.customize(config);
            }
        }

        return Redisson.create(config);
    }

    private String[] convert(List<String> nodesObject) {
        List<String> nodes = new ArrayList(nodesObject.size());
        Iterator var3 = nodesObject.iterator();

        while (true) {
            while (var3.hasNext()) {
                String node = (String) var3.next();
                if (!node.startsWith("redis://") && !node.startsWith("rediss://")) {
                    nodes.add("redis://" + node);
                } else {
                    nodes.add(node);
                }
            }

            return (String[]) nodes.toArray(new String[0]);
        }
    }

    private InputStream getConfigStream() throws IOException {
        Resource resource = this.ctx.getResource(this.redissonProperties.getFile());
        InputStream is = resource.getInputStream();
        return is;
    }
}


