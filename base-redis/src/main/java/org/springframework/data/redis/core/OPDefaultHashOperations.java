package org.springframework.data.redis.core;

import com.openkeji.redis.utils.RedisBodyUtils;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPDefaultHashOperations<K, HK, HV> extends DefaultHashOperations<K, HK, HV> {

    public OPDefaultHashOperations(RedisTemplate<K, ?> template) {
        super(template);
    }

    @Override
    public void put(K key, HK hashKey, HV value) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        byte[] rawHashValue = rawHashValue(value);

        RedisBodyUtils.validate(rawHashValue, key);
        execute(connection -> {
            connection.hSet(rawKey, rawHashKey, rawHashValue);
            return null;
        });
    }

    @Override
    public Boolean putIfAbsent(K key, HK hashKey, HV value) {

        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        byte[] rawHashValue = rawHashValue(value);

        RedisBodyUtils.validate(rawHashValue, key);
        return execute(connection -> connection.hSetNX(rawKey, rawHashKey, rawHashValue));
    }

    @Override
    public HV get(K key, Object hashKey) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        byte[] rawHashValue = execute(connection -> connection.hGet(rawKey, rawHashKey));

        RedisBodyUtils.validate(rawHashValue, key);
        return (HV) rawHashValue != null ? deserializeHashValue(rawHashValue) : null;
    }

}
