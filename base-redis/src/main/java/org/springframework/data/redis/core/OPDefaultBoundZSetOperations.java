package org.springframework.data.redis.core;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPDefaultBoundZSetOperations<K, V> extends DefaultBoundZSetOperations<K, V> {

    public OPDefaultBoundZSetOperations(K key, RedisOperations<K, V> operations) {
        super(key, operations);
    }
}
