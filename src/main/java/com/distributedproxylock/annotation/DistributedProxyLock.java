package com.distributedproxylock.annotation;


import com.distributedproxylock.core.DistributedProxyLockCommonUtil;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedProxyLock {
    String key();
    long waitOut() default 60;
    long executeOut() default 60;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    boolean atuoRemove() default true;
    String suffixKeyTypeEnum() default DistributedProxyLockCommonUtil.NO_SUFFIX;
    String objectName() default "";
    String paramName() default "";
    String lockConnectionEnum() default DistributedProxyLockCommonUtil.REDISSON;
}