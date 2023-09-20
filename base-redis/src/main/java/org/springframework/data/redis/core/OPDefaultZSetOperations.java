package org.springframework.data.redis.core;

import com.openkeji.redis.utils.RedisBodyUtils;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.lang.Nullable;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPDefaultZSetOperations<K, V> extends DefaultZSetOperations<K, V> {

    public OPDefaultZSetOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    @Override
    public Boolean add(K key, V value, double score) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);

        RedisBodyUtils.validate(rawValue, key);
        return execute(connection -> connection.zAdd(rawKey, score, rawValue));
    }

    @Nullable
    protected Boolean add(K key, V value, double score, RedisZSetCommands.ZAddArgs args) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);

        RedisBodyUtils.validate(rawValue, key);
        return execute(connection -> connection.zAdd(rawKey, score, rawValue, args));
    }
}
