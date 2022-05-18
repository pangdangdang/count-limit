package com.redislock.core;

import com.core.CountLimitCommonUtil;
import com.redislock.annotation.RedisLock;
import com.redislock.core.inter.RedisLockService;
import com.redislock.exception.RedisLockException;
import com.redislock.util.RedisLockUtil;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RedisLockKeyServiceImpl implements RedisLockService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public String getKeyWithThreadLocal(ProceedingJoinPoint joinPoint, RedisLock redisLock) {
        String lockKey = redisLock.key();
        if (StringUtil.isBlank(RedisLockUtil.get())) {
            throw new RedisLockException("RedisLockUtil为空");
        }
        return lockKey;
    }


    @Override
    public String getKeyWithParam(ProceedingJoinPoint joinPoint, RedisLock redisLock) {
        String objectName = redisLock.objectName();
        if (StringUtil.isBlank(objectName)) {
            throw new RedisLockException("objectName为空");
        }
        String paramName = redisLock.paramName();
        Object[] args = joinPoint.getArgs();
        String[] objectNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Map<String, Object> objectHashMap = new HashMap<>();
        for (int i = 0; i < objectNames.length; i++) {
            objectHashMap.put(objectNames[i], args[i]);
        }
        if (!objectHashMap.containsKey(objectName)) {
            throw new RedisLockException("入参不包含该对象" + objectName);
        }
        Object o = objectHashMap.get(objectName);
        if (StringUtil.isBlank(paramName)) {
            return redisLock.key() + o.toString();
        }
        String lockKey = redisLock.key() + CountLimitCommonUtil.getFieldValueByName(paramName, o);
        return lockKey;
    }


    @Override
    public Object lockBySpringRedis(String lockKey, ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        try {
            if (redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey)) {
                redisTemplate.expire(lockKey, redisLock.executeOut(), redisLock.timeUnit());
                log.debug("代理加锁成功:{}", lockKey);
                return joinPoint.proceed();
            } else {
                log.debug("代理加锁失败:{}", lockKey);
            }
        } catch (InterruptedException e) {
            log.error("获取代理锁异常:{}", e);
            throw e;
        } finally {
            redisTemplate.delete(lockKey);
        }
        return null;

    }


    @Override
    public Object lockByRedisson(String lockKey, ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(redisLock.waitOut(), redisLock.executeOut(), redisLock.timeUnit())) {
                log.debug("代理加锁成功:{}", lockKey);
                return joinPoint.proceed();
            } else {
                log.debug("代理加锁失败:{}", lockKey);
            }
        } catch (InterruptedException e) {
            log.error("获取代理锁异常:{}", e);
            throw e;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("代理解锁:{}", lockKey);
            }
            //如果方法注解中开启自动清除，就去除
            if (redisLock.atuoRemove()) {
                RedisLockUtil.remove();
                log.debug("自动清除RedisLockUtil:{}", lockKey);
            }
        }
        return null;

    }
}