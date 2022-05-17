package com.redislock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * redis锁注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    String key();
    long waitOut() default 60;
    long executeOut() default 60;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    boolean atuoRemove() default true;
    String suffixKeyTypeEnum() default "no_suffix";
    String objectName() default "";
    String paramName() default "";
    String redisEnum() default "redisson";
}