/*
 * Copyright 2011-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.core;

import com.openkeji.redis.utils.RedisBodyUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class OPDefaultListOperations<K, V> extends DefaultListOperations<K, V> {

    public OPDefaultListOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    @Override
    public List<V> range(K key, long start, long end) {
        throw new RuntimeException("list range api不开放");
    }

    @Override
    public void trim(K key, long start, long end) {
        throw new RuntimeException("list trim api不开放");
    }

    @Override
    public V index(K key, long index) {
        return execute(new ValueDeserializingRedisCallback(key) {

            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                byte[] results = connection.lIndex(rawKey, index);
                RedisBodyUtils.validate(results, key);
                return results;
            }
        }, true);
    }

    @Override
    public V leftPop(K key) {
        return execute(new ValueDeserializingRedisCallback(key) {

            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                byte[] results = connection.lPop(rawKey);
                RedisBodyUtils.validate(results, key);
                return results;
            }
        }, true);
    }

    @Override
    public V leftPop(K key, long timeout, TimeUnit unit) {
        int tm = (int) TimeoutUtils.toSeconds(timeout, unit);
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                List<byte[]> lPop = connection.bLPop(tm, rawKey);
                RedisBodyUtils.validate(lPop, key);
                return (CollectionUtils.isEmpty(lPop) ? null : lPop.get(1));
            }
        }, true);
    }

    @Override
    public V rightPop(K key) {
        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                byte[] results = connection.rPop(rawKey);
                RedisBodyUtils.validate(results, key);
                return results;
            }
        }, true);
    }

    @Override
    public V rightPop(K key, long timeout, TimeUnit unit) {
        int tm = (int) TimeoutUtils.toSeconds(timeout, unit);

        return execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                List<byte[]> bRPop = connection.bRPop(tm, rawKey);
                RedisBodyUtils.validate(bRPop, key);
                return (CollectionUtils.isEmpty(bRPop) ? null : bRPop.get(1));
            }
        }, true);
    }
}
