package com.openkeji.redis.template;

import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.OPDefaultBoundListOperations;
import org.springframework.data.redis.core.OPDefaultBoundSetOperations;
import org.springframework.data.redis.core.OPDefaultBoundValueOperations;
import org.springframework.data.redis.core.OPDefaultBoundZSetOperations;
import org.springframework.data.redis.core.OPDefaultListOperations;
import org.springframework.data.redis.core.OPDefaultSetOperations;
import org.springframework.data.redis.core.OPDefaultValueOperations;
import org.springframework.data.redis.core.OPDefaultZSetOperations;
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
    public ListOperations<K, V> opsForList() {
        if (listOps == null) {
            listOps = new OPDefaultListOperations<>(this);
        }
        return listOps;
    }

    @Override
    public SetOperations<K, V> opsForSet() {
        if (setOps == null) {
            setOps = new OPDefaultSetOperations<>(this);
        }
        return setOps;
    }

    @Override
    public ZSetOperations<K, V> opsForZSet() {
        if (zSetOps == null) {
            zSetOps = new OPDefaultZSetOperations<>(this);
        }
        return super.opsForZSet();
    }

    @Override
    public BoundValueOperations<K, V> boundValueOps(K key) {
        return new OPDefaultBoundValueOperations<>(key, this);
    }

    @Override
    public BoundListOperations<K, V> boundListOps(K key) {
        return new OPDefaultBoundListOperations<>(key, this);
    }

    @Override
    public BoundSetOperations<K, V> boundSetOps(K key) {
        return new OPDefaultBoundSetOperations<>(key, this);
    }

    @Override
    public BoundZSetOperations<K, V> boundZSetOps(K key) {
        return new OPDefaultBoundZSetOperations<>(key, this);
    }
}
