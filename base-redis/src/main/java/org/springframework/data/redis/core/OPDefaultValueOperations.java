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
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;

import java.util.concurrent.TimeUnit;

public class OPDefaultValueOperations<K, V> extends DefaultValueOperations<K, V> {

    public OPDefaultValueOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    @Override
    public V get(Object key) {

        return this.execute(new AbstractOperations<K, V>.ValueDeserializingRedisCallback(key) {
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                final byte[] bytes = connection.get(rawKey);
                RedisBodyUtils.validate(bytes, key);
                return bytes;
            }
        });
    }

    @Override
    public V getAndSet(K key, V newValue) {

        final byte[] rawValue = this.rawValue(newValue);
        return this.execute(new AbstractOperations<K, V>.ValueDeserializingRedisCallback(key) {
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                final byte[] bytes = connection.getSet(rawKey, rawValue);
                RedisBodyUtils.validate(bytes, key);
                return bytes;
            }
        });
    }

    @Override
    public void set(K key, V value) {

        byte[] rawValue = rawValue(value);
        RedisBodyUtils.validate(rawValue, key);
        execute(new ValueDeserializingRedisCallback(key) {
            @Override
            protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
                connection.set(rawKey, rawValue);
                return null;
            }
        });
    }

    @Override
    public void set(K key, V value, long timeout, TimeUnit unit) {

        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);

        RedisBodyUtils.validate(rawValue, key);
        execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                potentiallyUsePsetEx(connection);
                return null;
            }

            public void potentiallyUsePsetEx(RedisConnection connection) {

                if (!TimeUnit.MILLISECONDS.equals(unit) || !failsafeInvokePsetEx(connection)) {
                    connection.setEx(rawKey, TimeoutUtils.toSeconds(timeout, unit), rawValue);
                }
            }

            private boolean failsafeInvokePsetEx(RedisConnection connection) {

                boolean failed = false;
                try {
                    connection.pSetEx(rawKey, timeout, rawValue);
                } catch (UnsupportedOperationException e) {
                    // in case the connection does not support pSetEx return false to allow fallback to other operation.
                    failed = true;
                }
                return !failed;
            }

        });
    }
}
