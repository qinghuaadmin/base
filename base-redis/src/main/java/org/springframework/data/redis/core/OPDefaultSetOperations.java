package org.springframework.data.redis.core;

import com.openkeji.redis.utils.RedisBodyUtils;
import org.springframework.data.redis.connection.RedisConnection;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class OPDefaultSetOperations<K, V> extends DefaultSetOperations<K, V> {

    public OPDefaultSetOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    @Override
    public Set<V> members(K key) {
        throw new RuntimeException("set members api不开放");
    }

    @Override
    public Cursor<V> scan(K key, ScanOptions options) {
        throw new RuntimeException("set scan api不开放");
    }

    @Override
    public V pop(K key) {
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                byte[] results = connection.sPop(rawKey);
                RedisBodyUtils.validate(results, key);
                return results;
            }
        }, true);
    }

    @Override
    public List<V> pop(K key, long count) {
        byte[] rawKey = rawKey(key);
        List<byte[]> rawValues = execute(connection -> connection.sPop(rawKey, count), true);

        RedisBodyUtils.validate(rawValues, key);
        return deserializeValues(rawValues);
    }

    @Override
    public Set<V> intersect(K key, K otherKey) {
        throw new RuntimeException("set intersect api不开放");
    }

    @Override
    public Set<V> intersect(K key, Collection<K> otherKeys) {
        throw new RuntimeException("set intersect api不开放");
    }

    @Override
    public Set<V> union(K key, K otherKey) {
        throw new RuntimeException("set union api不开放");
    }

    @Override
    public Set<V> union(K key, Collection<K> otherKeys) {
        throw new RuntimeException("set union api不开放");
    }
}
