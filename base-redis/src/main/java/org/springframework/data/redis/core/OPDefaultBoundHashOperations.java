package org.springframework.data.redis.core;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-20
 */
public class OPDefaultBoundHashOperations<H, HK, HV> extends DefaultBoundHashOperations<H, HK, HV> {

    public OPDefaultBoundHashOperations(H key, RedisOperations<H, ?> operations) {
        super(key, operations);
    }
}
