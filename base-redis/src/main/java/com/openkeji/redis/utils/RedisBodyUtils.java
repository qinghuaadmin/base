package com.openkeji.redis.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Objects;

@Slf4j
public class RedisBodyUtils {

    private static final int MAX_BODY_SIZE = 1024 * 1024;

    public static final String ERROR_MSG_TPL = "The redis cache value is greater than 1M[{0}]";

    public static void validate(byte[] results, Object key) {
        if (results != null && results.length >= MAX_BODY_SIZE) {
            log.info(new RuntimeException(MessageFormat.format(ERROR_MSG_TPL, key)).toString());
        }
    }

    public static void validate(Collection<byte[]> results, Object key) {
        if (CollectionUtils.isEmpty(results)) {
            return;
        }

        final int total = results.stream()
                .filter(Objects::nonNull)
                .mapToInt(e -> e.length)
                .sum();
        if (total >= MAX_BODY_SIZE) {
            log.info(new RuntimeException(MessageFormat.format(ERROR_MSG_TPL, key)).toString());
        }
    }
}
