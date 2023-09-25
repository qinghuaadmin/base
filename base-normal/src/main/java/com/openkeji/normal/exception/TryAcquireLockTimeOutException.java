package com.openkeji.normal.exception;

/**
 * @program: base
 * @description:
 * @author: houqh
 * @create: 2023-09-19
 */
public class TryAcquireLockTimeOutException extends RuntimeException {
    public TryAcquireLockTimeOutException() {
    }

    public TryAcquireLockTimeOutException(String msg) {
        super(msg);
    }
}
