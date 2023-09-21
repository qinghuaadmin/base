package com.openkeji.redis.manager;

import com.openkeji.redis.common.DistributedLockKeyPrefixEnum;
import com.openkeji.redis.lock.DistributedLockTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundValueOperations;

import java.io.Serializable;

/**
 * @program: base
 * @description:
 * @author: kyle.hou
 * @create: 2023-09-21-08
 */
@SuppressWarnings("unchecked")
public abstract class AbstractValueCacheManager<V, PK extends Serializable> extends AbstractCacheManager<PK> {

    @Autowired(required = false)
    private DistributedLockTemplate distributedLockTemplate;

    /**
     * 穿透保护：指定时间内设置
     */
    protected boolean penetrateProtect = false;
    protected int penetrateProtectMillisecond;

    /**
     * 穿透保护时间
     */
    /**
     * 悲观锁实现
     * 启用createObject写锁,防止重复写入 {@linkplain AbstractValueCacheManager#createObject(Serializable)}
     */
    protected boolean createObjectWriteLock = false;

    public AbstractValueCacheManager() {
        super(DataType.STRING);
    }

    public V get(PK id) {
        final BoundValueOperations<PK, V> operations = getOperations(id);
        V object = operations.get();
        if (object == null) {
            // 缓存保护
            if (penetrateProtect && super.hasKey(id)) {
                return null;
            }

            object = getObject(id);
            if (object == null) {
                if (!createObjectWriteLock) {
                    object = createObject(id);
                } else {
                    object = distributedLockTemplate.executeWithRLock(DistributedLockKeyPrefixEnum.CREATEOBJECT_WRITELOCK, String.valueOf(id), () -> {
                        final V obj = getObject(id);
                        if (obj != null) {
                            return obj;
                        }
                        return createObject(id);
                    });
                }
            }
        }
        operations.set(object, expireTime, expireTimeUnit);
        return object;
    }

    /**
     * 获取Operations
     */
    protected BoundValueOperations<PK, V> getOperations(PK id) {
        final String fullCacheKey = this.makeFullCacheKey(id);
        return redisTemplate.boundValueOps(fullCacheKey);
    }

    /**
     * 子类覆写
     */
    protected V getObject(PK id) {
        return null;
    }

    /**
     * 子类覆写
     */
    protected V createObject(PK id) {
        return null;
    }
}
