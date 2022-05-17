package com.core;

import com.annotation.CountLimit;
import com.exception.CountLimitException;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Aspect
@Component
public class CountLimitAspect<T> {

    private static ReentrantLock lock = new ReentrantLock();

    private static volatile ConcurrentHashMap<String, Integer> countMap = new ConcurrentHashMap<>();

    @Value("${dcc.node.id}")
    private int workerId;

    @Pointcut("@annotation(com.annotation.CountLimit)")
    public void lockPointCut() {

    }

    @Around("lockPointCut() && @annotation(countLimit)")
    public Object around(ProceedingJoinPoint joinPoint, CountLimit countLimit) throws Throwable {
        LocalDateTime start = LocalDateTime.now();
        String inter = joinPoint.getTarget().getClass().getName() + joinPoint.getSignature().getName();
        String objectName = countLimit.objectName();
        String par = countLimit.paramName();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Map<String, Object> param = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], args[i]);
        }

        //获取限制的参数
        List<T> queryPar;
        String key = inter + objectName;
        int count;
        try {
            if (StringUtil.isBlank(par)) {
                //如果没有设置参数，说明在入参对象中
                queryPar = (List<T>) param.get(objectName);
            } else {
                //说明在入参的某个对象中，有一个参数是进行限流
                Object o = param.get(objectName);
                queryPar = (List<T>) CommonUtil.getFieldValueByName(par, o);
                key += par;
            }
            count = queryPar.size();
            while (!this.checkExceed(key, count, countLimit.limit())) {
                //是否超出等待时间
                if (start.plusSeconds(countLimit.waitTime()).isBefore(LocalDateTime.now())) {
                    throw new CountLimitException("超出等待时间" + key);
                }
                //将等待时长划分为20份
                Thread.sleep(countLimit.waitTime() * 1000 / 20);
            }
        } catch (Exception e) {
            throw new CountLimitException("计算限流异常", e);
        }

        try {
            return joinPoint.proceed();
        } finally {
            this.reduce(key, count);
        }
    }

    /**
     * 检查是否超出计算限制
     *
     * @param key
     * @param count
     * @param limit
     * @return
     */
    public boolean checkExceed(String key, int count, int limit) {
        try {
            if (lock.tryLock()) {
                int now = countMap.getOrDefault(workerId + key, 0);
                if (now + count > limit) {
                    return false;
                } else {
                    log.info("CountLimitAspect:{}增加计算量:{}", workerId + key, now + count);
                    countMap.put(workerId + key, now + count);
                    return true;
                }
            } else {
                return false;
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    /**
     * 减少目前在查询中的参数量级
     *
     * @param key
     * @param count
     */
    public void reduce(String key, int count) {
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                int now = countMap.get(workerId + key);
                log.info("CountLimitAspect:{}减少计算量:{}", workerId + key, now - count);
                countMap.put(workerId + key, now - count);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}