package com.core;

import com.exception.CountLimitException;
import com.redislock.annotation.RedisLock;
import com.redislock.enums.RedisEnum;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 共用类
 */
public class CountLimitCommonUtil {

    public static final String COUNT_LIMIT_LOCK = "COUNT_LIMIT_LOCK:";

    public static final String COUNT_LIMIT_REDIS_NODE_LOCK = "COUNT_LIMIT_REDIS_NODE_LOCK:";

    public static final String COUNT_LIMIT_REDIS_NODE_STORE = "COUNT_LIMIT_REDIS_NODE_STORE:";

    public static final String COUNT_LIMIT_STORE = "COUNT_LIMIT_STORE:";

    public static final String LOCAL_LOCK_STORE = "local_lock_store";

    public static final String LOCAL_LOCK_REDISSON_STORE = "local_lock_redisson_store";

    public static final String LOCAL_LOCK_SPRING_REDIS_STORE = "local_lock_spring_redis_store";

    public static final String REDISSON_LOCK_STORE = "redisson_lock_store";

    public static final String REDISSON_LOCK_REDISSON_STORE = "redisson_lock_redisson_store";

    public static final String SPRING_REDIS_LOCK_STORE = "spring_redis_lock_store";

    public static final String SPRING_REDIS_LOCK_SPRING_REDIS_STORE = "spring_redis_lock_spring_redis_store";

    private static volatile String NODE_ID;

    public static volatile int SPILT_SLEEP = 20;

    public static final String GET = "get";

    public static String getNodeId() {
        return NODE_ID;
    }

    public static synchronized void setNodeId(String nodeId) {
        NODE_ID = nodeId;
    }

    public static synchronized void setSpiltSleep(int spiltSleep) {
        SPILT_SLEEP = spiltSleep;
    }

    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = GET + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            throw new CountLimitException("获取属性值失败", e);
        }
    }

}