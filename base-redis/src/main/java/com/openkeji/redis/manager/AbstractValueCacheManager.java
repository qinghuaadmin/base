package com.openkeji.redis.manager;

import com.openkeji.normal.enums.redis.CommonCacheNameEnum;
import com.openkeji.normal.enums.redis.CommonDistributedLockCacheNamePrefixEnum;
import com.openkeji.redis.lock.DistributedLockTemplate;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundValueOperations;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * @program: base
 * @description:
 * @author: kyle.hou
 * @create: 2023-09-21-08
 */
@SuppressWarnings("unchecked")
public abstract class AbstractValueCacheManager<PK extends Serializable, V> extends AbstractCacheManager<PK> {

    @Autowired(required = false)
    private DistributedLockTemplate distributedLockTemplate;

    /**
     * 穿透保护：指定时间内设置
     * 默认5秒
     */
    @Setter
    protected boolean penetrateProtectEnable = false;

    /**
     * 穿透保护时间 单位:毫秒
     */
    @Setter
    protected int penetrateProtectMillisecond = 5000;

    /**
     * 穿透保护key
     */
    private static final String PENETRATE_PROTECT_KEY_TPL = CommonCacheNameEnum.BASE_PENETRATE_PROTECT.getCacheNamePrefix() + "{0}";

    /**
     * 悲观锁实现
     * 启用createObject写锁,防止重复写入 {@linkplain AbstractValueCacheManager#createObject(Serializable)}
     */
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
            if (Boolean.TRUE.equals(penetrateProtectEnable)) { // 缓存保护
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
     * 整数-自增
     *
     * @param id key
     */
    public Long increment(PK id) {
        return increment(id, 1);
    }

    /**
     * 整数-自增
     *
     * @param id   key
     * @param step 步长
     */
    public Long increment(PK id, long step) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        return boundKeyOperations.increment(step);
    }

    /**
     * 更新时，自动续期
     */
    protected boolean updateExpireTimeWhenUpdate() {
        return Boolean.TRUE;
    }

    /**
     * 更新
     *
     * @param id     key
     * @param object obj
     */
    public void update(PK id, V object) {
        update(id, object, getExpireTime(), getExpireTimeUnit());
    }

    /**
     * 更新
     *
     * @param id             key
     * @param object         obj
     * @param expireTime     过期时长
     * @param expireTimeUnit 过期时长时间单位
     */
    public void update(PK id, V object, int expireTime, TimeUnit expireTimeUnit) {
        final BoundValueOperations<PK, V> boundKeyOperations = getBoundKeyOperations(id);
        if (updateExpireTimeWhenUpdate()) {
            boundKeyOperations.set(object, expireTime, expireTimeUnit);
        } else {
            if (Boolean.TRUE.equals(hasKey(id))) {
                boundKeyOperations.set(object);
            } else {
                boundKeyOperations.set(object, expireTime, expireTimeUnit);
            }
        }
        // 更新之后,删除缓存穿透key
        deletePenetrateProtect(id);
    }
}
