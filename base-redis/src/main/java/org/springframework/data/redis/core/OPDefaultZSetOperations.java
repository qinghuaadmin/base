package org.springframework.data.redis.core;


public class OPDefaultZSetOperations<K, V> extends DefaultZSetOperations<K, V> {

    public OPDefaultZSetOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    @Override
    public Cursor<TypedTuple<V>> scan(K key, ScanOptions options) {
        throw new RuntimeException("zset scan api不开放");
    }

}
