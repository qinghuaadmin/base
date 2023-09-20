package org.springframework.data.redis.core;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPDefaultBoundListOperations<K, V> extends DefaultBoundListOperations<K, V> {

    public OPDefaultBoundListOperations(K key, RedisOperations<K, V> operations) {
        super(key, operations);
    }
}
