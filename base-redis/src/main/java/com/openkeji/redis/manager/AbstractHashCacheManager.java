package com.openkeji.redis.manager;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.OPDefaultHashOperations;
import org.springframework.data.redis.core.ScanOptions;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: base
 * @description:
 * @author: kyle.hou
 * @create: 2023-09-26-08
 */
@SuppressWarnings("unchecked")
public abstract class AbstractHashCacheManager<PK extends Serializable, HK, HV> extends AbstractCacheManager<PK> {

    public final OPDefaultHashOperations<PK, HK, HV> defaultHashOps;

    public AbstractHashCacheManager() {
        super(DataType.HASH);
        defaultHashOps = new OPDefaultHashOperations<PK, HK, HV>(redisTemplate);
    }

    @Override
    public BoundHashOperations<PK, HK, HV> boundKeyOps(PK id) {
        final String fullCacheKey = this.makeFullCacheKey(id);
        return redisTemplate.boundHashOps(fullCacheKey);
    }

    protected Long delete(PK id, Object... keys) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.delete(keys);
    }

    protected Boolean hasKey(PK id, Object key) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.hasKey(key);
    }

    protected HV get(PK id, Object member) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.get(member);
    }

    protected List<HV> multiGet(PK id, Collection<HK> keys) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.multiGet(keys);
    }

    protected Long increment(PK id, HK key, long delta) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.increment(key, delta);
    }

    protected Double increment(PK id, HK key, double delta) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.increment(key, delta);
    }

    protected HK randomKey(PK id) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomKey();
    }

    protected Map.Entry<HK, HV> randomEntry(PK id) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomEntry();
    }

    protected List<HK> randomKeys(PK id, long count) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomKeys(count);
    }

    protected Map<HK, HV> randomEntries(PK id, long count) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomEntries(count);
    }

    protected Set<HK> keys(PK id) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.keys();
    }

    protected Long lengthOfValue(PK id, HK hashKey) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.lengthOfValue(hashKey);
    }

    protected Long size(PK id) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.size();
    }

    protected void putAll(PK id, Map<? extends HK, ? extends HV> m) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        boundKeyOperations.putAll(m);
        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }
    }

    protected void put(PK id, HK key, HV value) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        boundKeyOperations.put(key, value);
        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }
    }

    protected Boolean putIfAbsent(PK id, HK key, HV value) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        final Boolean ifAbsent = boundKeyOperations.putIfAbsent(key, value);
        if (Boolean.TRUE.equals(ifAbsent) && Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            // 自动续期
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }
        return ifAbsent;
    }

    protected List<HV> values(PK id) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.values();
    }

    protected Map<HK, HV> entries(PK id) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.entries();
    }

    protected Cursor<Map.Entry<HK, HV>> scan(PK id, ScanOptions options) {
        final BoundHashOperations<PK, HK, HV> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.scan(options);
    }
}
