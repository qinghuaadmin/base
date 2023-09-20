package org.springframework.data.redis.core;


/**
 * @program: base
 * @description: 扩展
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPDefaultBoundValueOperations<K, V> extends DefaultBoundValueOperations<K, V> {

    public OPDefaultBoundValueOperations(K key, RedisOperations<K, V> operations) {
        super(key, operations);
    }
}
