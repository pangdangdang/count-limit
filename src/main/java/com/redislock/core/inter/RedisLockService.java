package com.redislock.core.inter;

import com.redislock.annotation.RedisLock;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @Author tingmailang
 */
public interface RedisLockService {

    String getKeyWithThreadLocal(ProceedingJoinPoint joinPoint, RedisLock redisLock);


    String getKeyWithParam(ProceedingJoinPoint joinPoint, RedisLock redisLock);

    Object lockByRedisson(String lockKey, ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable;

    Object lockBySpringRedis(String lockKey, ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable;

}
