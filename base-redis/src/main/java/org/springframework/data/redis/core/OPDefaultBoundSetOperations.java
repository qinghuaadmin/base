package org.springframework.data.redis.core;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPDefaultBoundSetOperations<K, V> extends DefaultBoundSetOperations<K, V> {

    public OPDefaultBoundSetOperations(K key, RedisOperations<K, V> operations) {
        super(key, operations);
    }
}
