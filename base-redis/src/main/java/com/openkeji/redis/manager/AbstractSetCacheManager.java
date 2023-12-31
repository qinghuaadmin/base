package com.openkeji.redis.manager;

import lombok.Getter;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.OPDefaultSetOperations;
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
 * @create: 2023-09-26-53
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSetCacheManager<PK extends Serializable, V> extends AbstractCacheManager<PK> {

    @Getter
    public final OPDefaultSetOperations<PK, V> defaultSetOps;

    public AbstractSetCacheManager() {
        super(DataType.SET);
        defaultSetOps = new OPDefaultSetOperations<PK, V>(redisTemplate);
    }

    @Override
    public BoundSetOperations<PK, V> boundKeyOps(PK id) {
        final String fullCacheKey = makeFullCacheKey(id);
        return redisTemplate.boundSetOps(fullCacheKey);
    }

    public Long add(PK id, V... values) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        final Long add = boundKeyOperations.add(values);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }

        return add;
    }

    public Long remove(PK id, Object... values) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        final Long remove = boundKeyOperations.remove(values);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }

        return remove;
    }

    public V pop(PK id) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        final V pop = boundKeyOperations.pop();

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }

        return pop;
    }

    public Boolean move(PK id, PK destKey, V value) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheDestKey = makeFullCacheKey(destKey);
        final Boolean move = boundKeyOperations.move((PK) fullCacheDestKey, value);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }

        return move;
    }

    public Long size(PK id) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.size();
    }

    public Boolean isMember(PK id, Object o) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.isMember(o);
    }

    public Map<Object, Boolean> isMember(PK id, Object... objects) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.isMember(objects);
    }

    public Set<V> intersect(PK id, PK key) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(key);
        return boundKeyOperations.intersect((PK) fullCacheKey);
    }

    public Set<V> intersect(PK id, Collection<PK> keys) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(keys);
        return boundKeyOperations.intersect((Collection<PK>) fullCacheKeyList);
    }

    public void intersectAndStore(PK id, PK key, PK destKey) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(key);
        final String fullCacheDestKey = makeFullCacheKey(destKey);
        boundKeyOperations.intersectAndStore((PK) fullCacheKey, (PK) fullCacheDestKey);
    }

    public void intersectAndStore(PK id, Collection<PK> keys, PK destKey) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(keys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);
        boundKeyOperations.intersectAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey);
    }


    public Set<V> union(PK id, PK key) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(key);
        return boundKeyOperations.union((PK) fullCacheKey);
    }


    public Set<V> union(PK id, Collection<PK> keys) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(keys);
        return boundKeyOperations.union((Collection<PK>) fullCacheKeyList);
    }


    public void unionAndStore(PK id, PK key, PK destKey) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(key);
        final String fullCacheDestKey = makeFullCacheKey(destKey);
        boundKeyOperations.unionAndStore((PK) fullCacheKey, (PK) fullCacheDestKey);
    }


    public void unionAndStore(PK id, Collection<PK> keys, PK destKey) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(keys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);
        boundKeyOperations.unionAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey);
    }


    public Set<V> diff(PK id, PK key) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(key);
        return boundKeyOperations.diff((PK) fullCacheKey);
    }


    public Set<V> diff(PK id, Collection<PK> keys) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(keys);
        return boundKeyOperations.diff((Collection<PK>) fullCacheKeyList);
    }

    public void diffAndStore(PK id, PK key, PK destKey) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(key);
        final String fullCacheDestKey = makeFullCacheKey(destKey);
        boundKeyOperations.diffAndStore((PK) fullCacheKey, (PK) fullCacheDestKey);
    }

    public void diffAndStore(PK id, Collection<PK> keys, PK destKey) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(keys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);
        boundKeyOperations.diffAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey);
    }

    public Set<V> members(PK id) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.members();
    }

    public V randomMember(PK id) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomMember();
    }

    public Set<V> distinctRandomMembers(PK id, long count) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.distinctRandomMembers(count);
    }

    public List<V> randomMembers(PK id, long count) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomMembers(count);
    }

    public Cursor<V> scan(PK id, ScanOptions options) {
        final BoundSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.scan(options);
    }
}
