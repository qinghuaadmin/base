package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;

import java.net.URI;
import java.net.URISyntaxException;

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

    /**
     * 覆写 {@linkplain RedisConnectionConfiguration#parseUrl(String)}
     * @param url
     * @return
     */
    protected ConnectionInfo parseUrlOverride(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisUrlSyntaxException(url);
            } else {
                boolean useSsl = "rediss".equals(scheme);
                String username = null;
                String password = null;
                if (uri.getUserInfo() != null) {
                    String candidate = uri.getUserInfo();
                    int index = candidate.indexOf(58);
                    if (index >= 0) {
                        username = candidate.substring(0, index);
                        password = candidate.substring(index + 1);
                    } else {
                        password = candidate;
                    }
                }

                return new ConnectionInfo(uri, useSsl, username, password);
            }
        } catch (URISyntaxException var9) {
            throw new RedisUrlSyntaxException(url, var9);
        }
    }

    /**
     * fuxie
     */
    public static class ConnectionInfo {
        private final URI uri;
        private final boolean useSsl;
        private final String username;
        private final String password;

        ConnectionInfo(URI uri, boolean useSsl, String username, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.username = username;
            this.password = password;
        }

        public boolean isUseSsl() {
            return this.useSsl;
        }

        public String getHostName() {
            return this.uri.getHost();
        }

        public int getPort() {
            return this.uri.getPort();
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }
    }
}
