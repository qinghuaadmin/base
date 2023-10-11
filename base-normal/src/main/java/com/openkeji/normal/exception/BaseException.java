package com.openkeji.normal.exception;

/**
 * @program: base
 * @description:
 * @author: kyle.hou
 * @create: 2023-10-11-09
 */
public abstract class BaseException extends RuntimeException {
    private int internalErrorCode;
    private String internalErrorMessage;
    private int errorCode;
    private String errorMessage;
    private Object data;
    private int httpCode;
    private String customInfo;

}
