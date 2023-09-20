package com.openkeji.redis.manager;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.OPDefaultBoundValueOperations;
import org.springframework.data.redis.core.OPDefaultValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPStringRedisTemplate extends StringRedisTemplate {

    private final ValueOperations<String, String> valueOps;

    public OPStringRedisTemplate() {
        this.valueOps = new OPDefaultValueOperations<>(this);
        this.setKeySerializer(RedisSerializer.string());
        this.setValueSerializer(RedisSerializer.string());
        this.setHashKeySerializer(RedisSerializer.string());
        this.setHashValueSerializer(RedisSerializer.string());
    }

    public OPStringRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    @Override
    public ValueOperations<String, String> opsForValue() {
        return valueOps;
    }

    @Override
    public BoundValueOperations<String, String> boundValueOps(String key) {
        return new OPDefaultBoundValueOperations<>(key, this);
    }
}
