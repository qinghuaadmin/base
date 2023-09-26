package com.openkeji.redis.manager;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.OPDefaultHashOperations;
import org.springframework.data.redis.core.OPDefaultZSetOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

import java.io.Serializable;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program: base
 * @description:
 * @author: kyle.hou
 * @create: 2023-09-26-53
 */
@SuppressWarnings("unchecked")
public abstract class AbstractZSetCacheManager<PK extends Serializable, V> extends AbstractCacheManager<PK> {

    private final OPDefaultZSetOperations<PK, V> defaultZSetOps;

    public AbstractZSetCacheManager(DataType dataType) {
        super(DataType.ZSET);
        defaultZSetOps = new OPDefaultZSetOperations<PK, V>(redisTemplate);
    }

    @Override
    protected BoundZSetOperations<PK, V> boundKeyOps(PK id) {
        final String fullCacheKey = makeFullCacheKey(id);
        return redisTemplate.boundZSetOps(fullCacheKey);
    }

    public Boolean add(PK id, V value, double score) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        final Boolean add = boundKeyOperations.add(value, score);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }

        return add;
    }

    public Boolean addIfAbsent(PK id, V value, double score) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        final Boolean ifAbsent = boundKeyOperations.addIfAbsent(value, score);

        if (Boolean.TRUE.equals(ifAbsent) && Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }

        return ifAbsent;
    }

    public Long add(PK id, Set<ZSetOperations.TypedTuple<V>> typedTuples) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        final Long add = boundKeyOperations.add(typedTuples);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }

        return add;
    }

    public Long addIfAbsent(PK id, Set<ZSetOperations.TypedTuple<V>> typedTuples) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        final Long addIfAbsent = boundKeyOperations.addIfAbsent(typedTuples);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }

        return addIfAbsent;
    }

    public Long remove(PK id, Object... values) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        final Long remove = boundKeyOperations.remove(values);

        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }

        return remove;
    }

    public Double incrementScore(PK id, V value, double delta) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.incrementScore(value, delta);
    }

    public V randomMember(PK id) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomMember();
    }

    public Set<V> distinctRandomMembers(PK id, long count) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.distinctRandomMembers(count);
    }

    public List<V> randomMembers(PK id, long count) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomMembers(count);
    }

    public ZSetOperations.TypedTuple<V> randomMemberWithScore(PK id) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomMemberWithScore();
    }

    public Set<ZSetOperations.TypedTuple<V>> distinctRandomMembersWithScore(PK id, long count) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.distinctRandomMembersWithScore(count);
    }

    public List<ZSetOperations.TypedTuple<V>> randomMembersWithScore(PK id, long count) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.randomMembersWithScore(count);
    }

    public Long rank(PK id, Object o) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.rank(o);
    }

    public Long reverseRank(PK id, Object o) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.reverseRank(o);
    }

    public Set<V> range(PK id, long start, long end) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.range(start, end);
    }

    public Set<ZSetOperations.TypedTuple<V>> rangeWithScores(PK id, long start, long end) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.rangeWithScores(start, end);
    }

    public Set<V> rangeByScore(PK id, double min, double max) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.rangeByScore(min, max);
    }

    public Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(PK id, double min, double max) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.rangeByScoreWithScores(min, max);
    }

    public Set<V> reverseRange(PK id, long start, long end) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.reverseRange(start, end);
    }

    public Set<ZSetOperations.TypedTuple<V>> reverseRangeWithScores(PK id, long start, long end) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.reverseRangeWithScores(start, end);
    }

    public Set<V> reverseRangeByScore(PK id, double min, double max) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.reverseRangeByScore(min, max);
    }

    public Set<ZSetOperations.TypedTuple<V>> reverseRangeByScoreWithScores(PK id, double min, double max) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.reverseRangeByScoreWithScores(min, max);
    }

    public Long count(PK id, double min, double max) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.count(min, max);
    }

    public Long lexCount(PK id, RedisZSetCommands.Range range) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.lexCount(range);
    }

    public ZSetOperations.TypedTuple<V> popMin(PK id) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.popMin();
    }

    public Set<ZSetOperations.TypedTuple<V>> popMin(PK id, long count) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.popMin(count);
    }

    public ZSetOperations.TypedTuple<V> popMin(PK id, long timeout, TimeUnit unit) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.popMin(timeout, unit);
    }

    public ZSetOperations.TypedTuple<V> popMin(PK id, Duration timeout) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.popMin(timeout);
    }

    public ZSetOperations.TypedTuple<V> popMax(PK id, Duration timeout) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.popMax(timeout);
    }

    public ZSetOperations.TypedTuple<V> popMax(PK id) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.popMax();
    }

    public Set<ZSetOperations.TypedTuple<V>> popMax(PK id, long count) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.popMax(count);
    }

    public ZSetOperations.TypedTuple<V> popMax(PK id, long timeout, TimeUnit unit) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.popMax(timeout, unit);
    }

    public Long size(PK id) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.size();
    }

    public Long zCard(PK id) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.zCard();
    }

    public Double score(PK id, Object o) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.score(o);
    }

    public List<Double> score(PK id, Object... o) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.score(o);
    }

    public Long removeRange(PK id, long start, long end) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.removeRange(start, end);
    }

    public Long removeRangeByLex(PK id, RedisZSetCommands.Range range) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.removeRangeByLex(range);
    }

    public Long removeRangeByScore(PK id, double min, double max) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.removeRangeByScore(min, max);
    }

    public Long unionAndStore(PK id, Collection<PK> otherKeys, PK destKey, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);
        return boundKeyOperations.unionAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey, aggregate, weights);
    }

    public Set<V> difference(PK id, Collection<PK> otherKeys) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        return boundKeyOperations.difference((Collection<PK>) fullCacheKeyList);
    }

    public Set<ZSetOperations.TypedTuple<V>> differenceWithScores(PK id, Collection<PK> otherKeys) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        return boundKeyOperations.differenceWithScores((Collection<PK>) fullCacheKeyList);
    }

    public Long differenceAndStore(PK id, Collection<PK> otherKeys, PK destKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);
        return boundKeyOperations.differenceAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey);
    }

    public Set<V> intersect(PK id, Collection<PK> otherKeys) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        return boundKeyOperations.intersect((Collection<PK>) fullCacheKeyList);
    }

    public Set<ZSetOperations.TypedTuple<V>> intersectWithScores(PK id, Collection<PK> otherKeys) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        return boundKeyOperations.intersectWithScores((Collection<PK>) fullCacheKeyList);
    }

    public Set<ZSetOperations.TypedTuple<V>> intersectWithScores(PK id, Collection<PK> otherKeys, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);

        return boundKeyOperations.intersectWithScores((Collection<PK>) fullCacheKeyList, aggregate, weights);
    }

    public Long intersectAndStore(PK id, PK otherKey, PK destKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(otherKey);
        final String fullCacheDestKey = makeFullCacheKey(destKey);

        return boundKeyOperations.intersectAndStore((PK) fullCacheKey, (PK) fullCacheDestKey);
    }

    public Long intersectAndStore(PK id, Collection<PK> otherKeys, PK destKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);

        return boundKeyOperations.intersectAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey);
    }

    public Long intersectAndStore(PK id, Collection<PK> otherKeys, PK destKey, RedisZSetCommands.Aggregate aggregate) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);

        return boundKeyOperations.intersectAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey);
    }

    public Long intersectAndStore(PK id, Collection<PK> otherKeys, PK destKey, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);

        return boundKeyOperations.intersectAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey, aggregate, weights);
    }

    public Set<V> union(PK id, Collection<PK> otherKeys) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);

        return boundKeyOperations.union((Collection<PK>) fullCacheKeyList);
    }

    public Set<ZSetOperations.TypedTuple<V>> unionWithScores(PK id, Collection<PK> otherKeys) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);

        return boundKeyOperations.unionWithScores((Collection<PK>) fullCacheKeyList);
    }

    public Set<ZSetOperations.TypedTuple<V>> unionWithScores(PK id, Collection<PK> otherKeys, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);

        return boundKeyOperations.unionWithScores((Collection<PK>) fullCacheKeyList, aggregate, weights);
    }

    public Long unionAndStore(PK id, PK otherKey, PK destKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(otherKey);
        final String fullCacheDestKey = makeFullCacheKey(destKey);

        return boundKeyOperations.unionAndStore((PK) fullCacheKey, (PK) fullCacheDestKey);
    }

    public Long unionAndStore(PK id, Collection<PK> otherKeys, PK destKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);

        return boundKeyOperations.unionAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey);
    }

    public Long unionAndStore(PK id, Collection<PK> otherKeys, PK destKey, RedisZSetCommands.Aggregate aggregate) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final List<String> fullCacheKeyList = makeFullCacheKey(otherKeys);
        final String fullCacheDestKey = makeFullCacheKey(destKey);

        return boundKeyOperations.unionAndStore((Collection<PK>) fullCacheKeyList, (PK) fullCacheDestKey, aggregate);
    }

    public Cursor<ZSetOperations.TypedTuple<V>> scan(PK id, ScanOptions options) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.scan(options);
    }

    public Set<V> rangeByLex(PK id, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.rangeByLex(range, limit);
    }

    public Set<V> reverseRangeByLex(PK id, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);
        return boundKeyOperations.reverseRangeByLex(range, limit);
    }

    public Set<V> difference(PK id, PK otherKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(otherKey);

        return boundKeyOperations.difference((PK) fullCacheKey);
    }

    public Set<ZSetOperations.TypedTuple<V>> differenceWithScores(PK id, PK otherKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(otherKey);

        return boundKeyOperations.differenceWithScores((PK) fullCacheKey);
    }

    public Long differenceAndStore(PK id, PK otherKey, PK destKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(otherKey);
        final String fullCacheDestKey = makeFullCacheKey(destKey);

        return boundKeyOperations.differenceAndStore((PK) fullCacheKey, (PK) fullCacheDestKey);
    }

    public Set<V> intersect(PK id, PK otherKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(otherKey);

        return boundKeyOperations.intersect((PK) fullCacheKey);
    }

    public Set<ZSetOperations.TypedTuple<V>> intersectWithScores(PK id, PK otherKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(otherKey);

        return boundKeyOperations.intersectWithScores((PK) fullCacheKey);
    }

    public Set<V> union(PK id, PK otherKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(otherKey);

        return boundKeyOperations.union((PK) fullCacheKey);
    }

    public Set<ZSetOperations.TypedTuple<V>> unionWithScores(PK id, PK otherKey) {
        final BoundZSetOperations<PK, V> boundKeyOperations = boundKeyOps(id);

        final String fullCacheKey = makeFullCacheKey(otherKey);

        return boundKeyOperations.unionWithScores((PK) fullCacheKey);
    }
}
