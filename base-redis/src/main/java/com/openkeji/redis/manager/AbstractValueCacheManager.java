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
     * 穿透保护时间 单位:毫秒
     */
    @Setter
    @Getter
    protected int penetrateProtectMillisecond = 5000;

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
    public V get(PK id) {
        final BoundValueOperations<PK, V> operations = getBoundKeyOperations(id);
        V object = operations.get();

        String penetrateProtectKey = null;
        if (object == null) {
            if (Boolean.TRUE.equals(penetrateProtectEnable)) { // 缓存穿透保护
                penetrateProtectKey = MessageFormat.format(PENETRATE_PROTECT_KEY_TPL, id);
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
            if (Boolean.TRUE.equals(penetrateProtectEnable)) { // 赋占位值
                assert penetrateProtectKey != null;
                redisTemplate.opsForValue().set(penetrateProtectKey, 1, penetrateProtectMillisecond, TimeUnit.MILLISECONDS);
            }
        } else {
            operations.set(object, expireTime, expireTimeUnit);
        }
        return object;
    }

    /**
     * 删除缓存穿透key
     *
     * @param id key
     */
    final protected void deletePenetrateProtect(PK id) {
        final String penetrateProtectKey = MessageFormat.format(PENETRATE_PROTECT_KEY_TPL, id);
        redisTemplate.delete(penetrateProtectKey);
    }

    /**
     * 更新时，自动续期
     */
    protected boolean updateExpireTimeWhenUpdate() {
        return Boolean.TRUE;
    }

    public void set(PK id, V value) {
        set(id, value, getExpireTime(), getExpireTimeUnit());
    }

    public void set(PK id, V value, Duration timeout) {
        if (TimeoutUtils.hasMillis(timeout)) {
            set(id, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            set(id, value, timeout.getSeconds(), TimeUnit.SECONDS);
        }
    }

    public void set(PK id, V value, long timeout, TimeUnit unit) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        if (updateExpireTimeWhenUpdate()) {
            boundKeyOperations.set(value, timeout, unit);
        } else {
            if (Boolean.TRUE.equals(hasKey(id))) {
                boundKeyOperations.set(value);
            } else {
                boundKeyOperations.set(value, timeout, unit);
            }
        }
        // 更新之后,删除缓存穿透key
        deletePenetrateProtect(id);
    }

    public void set(PK id, V value, long offset) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        boundKeyOperations.set(value, offset);
        // 更新过期时间
        boundKeyOperations.expire(getExpireTime(), getExpireTimeUnit());
        // 更新之后,删除缓存穿透key
        deletePenetrateProtect(id);
    }

    public Boolean setIfAbsent(PK id, V value) {
        return setIfAbsent(id, value, getExpireTime(), getExpireTimeUnit());
    }

    public Boolean setIfAbsent(PK id, V value, Duration timeout) {
        if (TimeoutUtils.hasMillis(timeout)) {
            return setIfAbsent(id, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        }
        return setIfAbsent(id, value, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    public Boolean setIfAbsent(PK id, V value, long timeout, TimeUnit unit) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        final Boolean ifAbsent = boundKeyOperations.setIfAbsent(value, timeout, unit);
        if (Boolean.TRUE.equals(ifAbsent)) {
            // 更新之后,删除缓存穿透key
            deletePenetrateProtect(id);
        }
        return ifAbsent;
    }

    public Boolean setIfPresent(PK id, V value) {
        return setIfPresent(id, value, getExpireTime(), getExpireTimeUnit());
    }

    public Boolean setIfPresent(PK id, V value, Duration timeout) {
        if (TimeoutUtils.hasMillis(timeout)) {
            return setIfPresent(id, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        }
        return setIfPresent(id, value, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    public Boolean setIfPresent(PK id, V value, long timeout, TimeUnit unit) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.setIfPresent(value, timeout, unit);
    }

    public V getAndDelete(PK id) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndDelete();
    }

    public V getAndExpire(PK id, long timeout, TimeUnit unit) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndExpire(timeout, unit);
    }

    public V getAndExpire(PK id, Duration timeout) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndExpire(timeout);
    }

    public V getAndPersist(PK id) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndPersist();
    }

    public V getAndSet(PK id, V value) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.getAndSet(value);
    }

    public Long increment(PK id) {
        return increment(id, 1);
    }

    public Long increment(PK id, long delta) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.increment(delta);
    }

    public Double increment(PK id, double delta) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.increment(delta);
    }

    public Long decrement(PK id) {
        return decrement(id, 1);
    }

    public Long decrement(PK id, long delta) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.decrement(delta);
    }

    public Integer append(PK id, String value) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.append(value);
    }

    public String get(PK id, long start, long end) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.get(start, end);
    }

    public Long size(PK id) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.size();
    }
}
