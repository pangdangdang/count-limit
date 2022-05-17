package com.core;

import com.exception.CountLimitException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * 共用类
 */
@Service
public class CommonUtil {

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