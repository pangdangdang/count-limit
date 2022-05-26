package com.distributedproxylock.core.inter;

import com.distributedproxylock.annotation.DistributedProxyLock;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @Author tingmailang
 */
public interface DistributedProxyLockService {

    String getKeyWithThreadLocal(ProceedingJoinPoint joinPoint, DistributedProxyLock distributedProxyLock);


    String getKeyWithParam(ProceedingJoinPoint joinPoint, DistributedProxyLock distributedProxyLock);

    Object lockByRedisson(String lockKey, ProceedingJoinPoint joinPoint, DistributedProxyLock distributedProxyLock) throws Throwable;

    Object lockBySpringRedis(String lockKey, ProceedingJoinPoint joinPoint, DistributedProxyLock distributedProxyLock) throws Throwable;

}
