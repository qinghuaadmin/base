package com.openkeji.redis.exception;

/**
 * @program: sino-msg-notice-center
 * @description:
 * @author: houqh
 * @create: 2023-09-18
 */
public class RedisUrlSyntaxException extends RuntimeException {
    private final String url;

    public RedisUrlSyntaxException(String url, Exception cause) {
        super(buildMessage(url), cause);
        this.url = url;
    }

    public RedisUrlSyntaxException(String url) {
        super(buildMessage(url));
        this.url = url;
    }

    String getUrl() {
        return this.url;
    }

    private static String buildMessage(String url) {
        return "Invalid Redis URL '" + url + "'";
    }
}
