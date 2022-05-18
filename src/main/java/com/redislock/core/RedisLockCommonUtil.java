package com.redislock.core;

import com.redislock.exception.RedisLockException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * 共用类
 */
@Service
public class RedisLockCommonUtil {

    public static final String REDISSON = "redisson";

    public static final String SPRING_REDIS = "spring_redis";

    public static final String NO_SUFFIX = "no_suffix";

    public static final String THREAD_LOCAL = "thread_local";

    public static final String PARAM = "param";

    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            throw new RedisLockException("获取属性值失败" + e);
        }
    }
}