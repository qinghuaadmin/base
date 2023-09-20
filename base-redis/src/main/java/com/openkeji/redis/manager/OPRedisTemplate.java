package com.openkeji.redis.manager;

import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.OPDefaultBoundValueOperations;
import org.springframework.data.redis.core.OPDefaultValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPRedisTemplate<K, V> extends RedisTemplate<K, V> {

    private ValueOperations<K, V> valueOps;
    private ListOperations<K, V> listOps;
    private SetOperations<K, V> setOps;
    private ZSetOperations<K, V> zSetOps;

    @Override
    public ValueOperations<K, V> opsForValue() {
        if (valueOps == null) {
            valueOps = new OPDefaultValueOperations<>(this);
        }
        return valueOps;
    }

    @Override
    public BoundValueOperations<K, V> boundValueOps(K key) {
        return new OPDefaultBoundValueOperations<>(key, this);
    }


}
