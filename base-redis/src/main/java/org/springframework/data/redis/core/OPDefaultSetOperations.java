package org.springframework.data.redis.core;

import com.openkeji.redis.utils.RedisBodyUtils;
import org.springframework.data.redis.connection.RedisConnection;

import java.util.List;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPDefaultSetOperations<K, V> extends DefaultSetOperations<K, V> {

    public OPDefaultSetOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    @Override
    public V pop(K key) {
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                final byte[] bytes = connection.sPop(rawKey);
                RedisBodyUtils.validate(bytes, key);
                return bytes;
            }
        });
    }

    @Override
    public List<V> pop(K key, long count) {
        byte[] rawKey = rawKey(key);
        List<byte[]> rawValues = execute(connection -> connection.sPop(rawKey, count));
        RedisBodyUtils.validate(rawValues, key);
        return deserializeValues(rawValues);
    }
}
