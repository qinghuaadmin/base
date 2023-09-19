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
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public HV get(K key, Object hashKey) {
        byte[] rawKey = rawKey(key);
        byte[] rawHashKey = rawHashKey(hashKey);
        byte[] rawHashValue = execute(connection -> connection.hGet(rawKey, rawHashKey), true);

        RedisBodyUtils.validate(rawHashValue, key);
        return rawHashValue != null ? deserializeHashValue(rawHashValue) : null;
    }

    @Override
    public List<HV> multiGet(K key, Collection<HK> fields) {
        if (fields.isEmpty()) {
            return Collections.emptyList();
        }

        byte[] rawKey = rawKey(key);
        byte[][] rawHashKeys = new byte[fields.size()][];

        int counter = 0;
        for (HK hashKey : fields) {
            rawHashKeys[counter++] = rawHashKey(hashKey);
        }

        List<byte[]> rawValues = execute(connection -> connection.hMGet(rawKey, rawHashKeys));
        RedisBodyUtils.validate(rawValues, key);

        return deserializeHashValues(rawValues);
    }

    @Override
    public Set<HK> keys(K key) {
        throw new RuntimeException("hash keys api不开放");
    }

    @Override
    public Map<HK, HV> entries(K key) {
        byte[] rawKey = rawKey(key);
        Map<byte[], byte[]> entries = execute(connection -> connection.hGetAll(rawKey));
        if(MapUtils.isNotEmpty(entries)) {
            RedisBodyUtils.validate(entries.values(), key);
        }

        return entries != null ? deserializeHashMap(entries) : Collections.emptyMap();
    }

    @Override
    public Long delete(K key, Object... hashKeys) {
        throw new RuntimeException("hash delete api不开放");
    }

    @Override
    public Cursor<Map.Entry<HK, HV>> scan(K key, ScanOptions options) {
        throw new RuntimeException("hash scan api不开放");
    }
}
