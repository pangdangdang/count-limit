package com.distributedproxylock.core;

import com.distributedproxylock.annotation.DistributedProxyLock;
import com.distributedproxylock.core.inter.DistributedProxyLockService;
import com.distributedproxylock.exception.DistributedProxyLockException;
import com.distributedproxylock.util.DistributedProxyLockUtil;
import jodd.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class DistributedProxyLockKeyServiceImpl implements DistributedProxyLockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedProxyLockKeyServiceImpl.class);

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public String getKeyWithThreadLocal(ProceedingJoinPoint joinPoint, DistributedProxyLock distributedProxyLock) {
        String lockKey = distributedProxyLock.key();
        if (StringUtil.isBlank(DistributedProxyLockUtil.get())) {
            throw new DistributedProxyLockException("DistributedProxyLockUtil为空");
        }
        return lockKey;
    }


    @Override
    public String getKeyWithParam(ProceedingJoinPoint joinPoint, DistributedProxyLock distributedProxyLock) {
        String objectName = distributedProxyLock.objectName();
        if (StringUtil.isBlank(objectName)) {
            throw new DistributedProxyLockException("objectName为空");
        }
        String paramName = distributedProxyLock.paramName();
        Object[] args = joinPoint.getArgs();
        String[] objectNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Map<String, Object> objectHashMap = new HashMap<>();
        for (int i = 0; i < objectNames.length; i++) {
            objectHashMap.put(objectNames[i], args[i]);
        }
        if (!objectHashMap.containsKey(objectName)) {
            throw new DistributedProxyLockException("入参不包含该对象" + objectName);
        }
        Object o = objectHashMap.get(objectName);
        if (StringUtil.isBlank(paramName)) {
            return distributedProxyLock.key() + o.toString();
        }
        String lockKey = distributedProxyLock.key() + DistributedProxyLockCommonUtil.getFieldValueByName(paramName, o);
        return lockKey;
    }


    public Object lockBySpringRedis(String lockKey, ProceedingJoinPoint joinPoint, DistributedProxyLock distributedProxyLock) throws Throwable {
        try {
            if (redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey)) {
                redisTemplate.expire(lockKey, distributedProxyLock.executeOut(), distributedProxyLock.timeUnit());
                LOGGER.debug("代理加锁成功:{}", lockKey);
                return joinPoint.proceed();
            } else {
                LOGGER.debug("代理加锁失败:{}", lockKey);
            }
        } catch (InterruptedException e) {
            LOGGER.error("获取代理锁异常:{}", e);
            throw e;
        } finally {
            redisTemplate.delete(lockKey);
        }
        return null;

    }


    public Object lockByRedisson(String lockKey, ProceedingJoinPoint joinPoint, DistributedProxyLock distributedProxyLock) throws Throwable {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(distributedProxyLock.waitOut(), distributedProxyLock.executeOut(), distributedProxyLock.timeUnit())) {
                LOGGER.debug("代理加锁成功:{}", lockKey);
                return joinPoint.proceed();
            } else {
                LOGGER.debug("代理加锁失败:{}", lockKey);
            }
        } catch (InterruptedException e) {
            LOGGER.error("获取代理锁异常:{}", e);
            throw e;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                LOGGER.debug("代理解锁:{}", lockKey);
            }
            //如果方法注解中开启自动清除，就去除
            if (distributedProxyLock.atuoRemove()) {
                DistributedProxyLockUtil.remove();
                LOGGER.debug("自动清除DistributedProxyLockUtil:{}", lockKey);
            }
        }
        return null;

    }
}