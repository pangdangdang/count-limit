package com.core;

import com.exception.CountLimitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 共用类
 */
public class CountLimitCommonUtil {

    public static final String COUNT_LIMIT_LOCK = "COUNT_LIMIT_LOCK:";

    public static final String COUNT_LIMIT_STORE = "COUNT_LIMIT_STORE:";

    public static final String LOCAL_LOCK_STORE = "local_lock_store";
    public static final String LOCAL_LOCK_REDISSON_STORE = "local_lock_redisson_store";
    public static final String LOCAL_LOCK_SPRING_REDIS_STORE = "local_lock_spring_redis_store";
    public static final String REDISSON_LOCK_STORE = "redisson_lock_store";
    public static final String REDISSON_LOCK_REDISSON_STORE = "redisson_lock_redisson_store";
    public static final String SPRING_REDIS_LOCK_STORE = "spring_redis_lock_store";
    public static final String SPRING_REDIS_LOCK_SPRING_REDIS_STORE = "spring_redis_lock_spring_redis_store";

    private static volatile String NODE_ID;

    public static String getNodeId() {
        if (NODE_ID == null) {
            synchronized (NODE_ID) {
                //再次检查是否已有节点id
                if(NODE_ID == null){
                    NODE_ID = UUID.randomUUID().toString();
                }
            }

        }
        return NODE_ID;
    }

    public static synchronized void setNodeId(String nodeId) {
        NODE_ID = nodeId;
    }

    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            throw new CountLimitException("获取属性值失败" + e);
        }
    }

}