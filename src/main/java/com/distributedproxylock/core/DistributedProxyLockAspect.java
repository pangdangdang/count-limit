package com.distributedproxylock.core;

import com.distributedproxylock.annotation.DistributedProxyLock;
import com.distributedproxylock.core.inter.DistributedProxyLockService;
import com.distributedproxylock.enums.DistributedProxyLockSuffixKeyTypeEnum;
import com.distributedproxylock.enums.LockConnectionEnum;
import com.distributedproxylock.exception.DistributedProxyLockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 分布式锁，切面处理类
 */
@Aspect
@Component
public class DistributedProxyLockAspect {

    @Resource
    private DistributedProxyLockService distributedProxyLockService;

    @Pointcut("@annotation(com.distributedproxylock.annotation.DistributedProxyLock)")
    public void lockPointCut() {
    }

    @Around("lockPointCut() && @annotation(distributedProxyLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedProxyLock distributedProxyLock) throws Throwable {
        String lockKey;
        try {
            DistributedProxyLockSuffixKeyTypeEnum suffixKeyTypeEnum = DistributedProxyLockSuffixKeyTypeEnum.of(distributedProxyLock.suffixKeyTypeEnum());
            switch (suffixKeyTypeEnum) {
                case PARAM:
                    lockKey = distributedProxyLockService.getKeyWithParam(joinPoint, distributedProxyLock);
                    break;
                case NO_SUFFIX:
                    lockKey = distributedProxyLock.key();
                    break;
                case THREAD_LOCAL:
                    lockKey = distributedProxyLockService.getKeyWithThreadLocal(joinPoint, distributedProxyLock);
                    break;
                default:
                    throw new DistributedProxyLockException("未知后缀获取类型" + distributedProxyLock.suffixKeyTypeEnum());
            }
        } catch (Exception e) {
            throw new DistributedProxyLockException("获取后缀key失败" + e);
        }

        LockConnectionEnum lockConnectionEnum = LockConnectionEnum.of(distributedProxyLock.lockConnectionEnum());
        switch (lockConnectionEnum) {
            case REDISSON:
                return distributedProxyLockService.lockByRedisson(lockKey, joinPoint, distributedProxyLock);
            case SPRING_REDIS:
                return distributedProxyLockService.lockBySpringRedis(lockKey, joinPoint, distributedProxyLock);
            default:
                throw new DistributedProxyLockException("未知redis工具" + distributedProxyLock.lockConnectionEnum());
        }
    }

}