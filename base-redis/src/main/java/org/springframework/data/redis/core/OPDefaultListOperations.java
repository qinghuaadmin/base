package org.springframework.data.redis.core;

import com.openkeji.redis.utils.RedisBodyUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPDefaultListOperations<K, V> extends DefaultListOperations<K, V> {

    public OPDefaultListOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    @Override
    public V index(K key, long index) {

        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                final byte[] bytes = connection.lIndex(rawKey, index);
                RedisBodyUtils.validate(bytes, key);
                return bytes;
            }
        });
    }

    @Override
    public V leftPop(K key) {
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                final byte[] bytes = connection.lPop(rawKey);
                RedisBodyUtils.validate(bytes, key);
                return bytes;
            }
        });
    }

    @Override
    public V leftPop(K key, long timeout, TimeUnit unit) {
        int tm = (int) TimeoutUtils.toSeconds(timeout, unit);
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                List<byte[]> lPop = connection.bLPop(tm, rawKey);
                RedisBodyUtils.validate(lPop, key);
                return (CollectionUtils.isEmpty(lPop) ? null : lPop.get(1));
            }
        });
    }

    @Override
    public V rightPop(K key) {
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                final byte[] bytes = connection.rPop(rawKey);
                RedisBodyUtils.validate(bytes, key);
                return bytes;
            }
        });
    }

    @Override
    public V rightPop(K key, long timeout, TimeUnit unit) {
        int tm = (int) TimeoutUtils.toSeconds(timeout, unit);
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                List<byte[]> bRPop = connection.bRPop(tm, rawKey);
                RedisBodyUtils.validate(bRPop, key);
                return (CollectionUtils.isEmpty(bRPop) ? null : bRPop.get(1));
            }
        });
    }
}
