package com.openkeji.redis.manager;

import com.openkeji.normal.enums.redis.CommonCacheNameEnum;
import com.openkeji.normal.enums.redis.CommonDistributedLockCacheNamePrefixEnum;
import com.openkeji.redis.lock.DistributedLockTemplate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.TimeoutUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @program: base
 * @description:
 * @author: kyle.hou
 * @create: 2023-09-21-08
 */
@SuppressWarnings("unchecked")
public abstract class AbstractValueCacheManager<PK extends Serializable, V> extends AbstractCacheManager<PK> {

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    /**
     * 穿透保护：指定时间内设置
     * 默认5秒
     */
    @Setter
    @Getter
    protected boolean penetrateProtectEnable = false;

    /**
     * 穿透保护时间 单位:毫秒 不建议设置太久
     */
    @Setter
    @Getter
    protected int penetrateProtectMillisecond = 3000;

    /**
     * 穿透保护key
     */
    private static final String PENETRATE_PROTECT_KEY_TPL = CommonCacheNameEnum.BASE_PENETRATE_PROTECT.getCacheNamePrefix() + "{0}";

    /**
     * 悲观锁实现
     * 启用createObject写锁,防止重复写入 {@linkplain AbstractValueCacheManager#createObject(Serializable)}
     */
    @Setter
    @Getter
    protected boolean createObjectWriteLock = false;

    public AbstractValueCacheManager() {
        super(DataType.STRING);
    }

    /**
     * - 从DB获取数据
     * 子类覆写
     */
    protected V getObject(PK id) {
        return null;
    }

    /**
     * - 初始化DB数据
     * 子类覆写
     */
    protected V createObject(PK id) {
        return null;
    }

    /**
     * 获取Operations
     */
    protected BoundValueOperations<PK, V> getBoundKeyOperations(PK id) {
        final String fullCacheKey = this.makeFullCacheKey(id);
        return redisTemplate.boundValueOps(fullCacheKey);
    }

    /**
     * 从缓存读取数据
     *
     * @param id key
     */
    protected V get(PK id) {
        final BoundValueOperations<PK, V> operations = getBoundKeyOperations(id);
        V object = operations.get();

        String penetrateProtectKey = null;
        if (object == null) {
            if (Boolean.TRUE.equals(penetrateProtectEnable)) { // 缓存穿透保护
                penetrateProtectKey = getPenetrateProtectKey(id);
                if (Boolean.TRUE.equals(redisTemplate.hasKey(penetrateProtectKey))) {
                    return null;
                }
            }

            object = getObject(id);
            if (object == null) {
                if (Boolean.FALSE.equals(createObjectWriteLock)) {
                    object = createObject(id);
                } else {
                    object = distributedLockTemplate.executeWithRLock(CommonDistributedLockCacheNamePrefixEnum.BASE_CREATEOBJECT_WRITELOCK, String.valueOf(id), () -> {
                        final V obj = getObject(id);
                        if (obj != null) {
                            return obj;
                        }
                        return createObject(id);
                    });
                }
            }
        }

        if (object == null) {
            if (Boolean.TRUE.equals(penetrateProtectEnable)) { // 穿透保护 赋占位值
                assert penetrateProtectKey != null;
                redisTemplate.opsForValue().set(penetrateProtectKey, 1, penetrateProtectMillisecond, TimeUnit.MILLISECONDS);
            }
        } else {
            operations.set(object, expireTime, expireTimeUnit);
        }
        return object;
    }

    /**
     * 缓存穿透key
     *
     * @param id
     * @return
     */
    final protected String getPenetrateProtectKey(PK id) {
        return MessageFormat.format(PENETRATE_PROTECT_KEY_TPL, id);
    }

    /**
     * 删除缓存穿透key
     *
     * @param id key
     */
    final protected void deletePenetrateProtect(PK id) {
        redisTemplate.delete(getPenetrateProtectKey(id));
    }

    protected void set(PK id, V value) {
        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            set(id, value, getExpireTime(), getExpireTimeUnit());
        } else {
            final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
            boundKeyOperations.set(value);
        }
    }

    protected void set(PK id, V value, Duration timeout) {
        if (TimeoutUtils.hasMillis(timeout)) {
            set(id, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            set(id, value, timeout.getSeconds(), TimeUnit.SECONDS);
        }
    }

    protected void set(PK id, V value, long timeout, TimeUnit unit) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        boundKeyOperations.set(value, timeout, unit);
    }

    protected void set(PK id, V value, long offset) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        boundKeyOperations.set(value, offset);
        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            // 更新过期时间
            boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        }
    }

    protected Boolean setIfAbsent(PK id, V value) {
        boolean ifAbsent;
        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            ifAbsent = setIfAbsent(id, value, getExpireTime(), getExpireTimeUnit());
        } else {
            final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
            ifAbsent = Boolean.TRUE.equals(boundKeyOperations.setIfAbsent(value));
        }
        return ifAbsent;
    }

    protected Boolean setIfAbsent(PK id, V value, Duration timeout) {
        if (TimeoutUtils.hasMillis(timeout)) {
            return setIfAbsent(id, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        }
        return setIfAbsent(id, value, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    protected Boolean setIfAbsent(PK id, V value, long timeout, TimeUnit unit) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.setIfAbsent(value, timeout, unit);
    }

    protected Boolean setIfPresent(PK id, V value) {
        boolean ifPresent;
        if (Boolean.TRUE.equals(updateExpireTimeWhenUpdate())) {
            ifPresent = setIfPresent(id, value, getExpireTime(), getExpireTimeUnit());
        } else {
            final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
            ifPresent = Boolean.TRUE.equals(boundKeyOperations.setIfPresent(value));
        }
        return ifPresent;
    }

    protected Boolean setIfPresent(PK id, V value, Duration timeout) {
        if (TimeoutUtils.hasMillis(timeout)) {
            return setIfPresent(id, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        }
        return setIfPresent(id, value, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    protected Boolean setIfPresent(PK id, V value, long timeout, TimeUnit unit) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.setIfPresent(value, timeout, unit);
    }

    protected V getAndDelete(PK id) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndDelete();
    }

    protected V getAndExpire(PK id, long timeout, TimeUnit unit) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndExpire(timeout, unit);
    }

    protected V getAndExpire(PK id, Duration timeout) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndExpire(timeout);
    }

    protected V getAndPersist(PK id) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndPersist();
    }

    protected V getAndSet(PK id, V value) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndSet(value);
    }

    protected Long increment(PK id) {
        return increment(id, 1);
    }

    protected Long increment(PK id, long delta) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.increment(delta);
    }

    protected Double increment(PK id, double delta) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.increment(delta);
    }

    protected Long decrement(PK id) {
        return decrement(id, 1);
    }

    protected Long decrement(PK id, long delta) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.decrement(delta);
    }

    protected Integer append(PK id, String value) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.append(value);
    }

    protected String get(PK id, long start, long end) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.get(start, end);
    }

    protected Long size(PK id) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.size();
    }
}
