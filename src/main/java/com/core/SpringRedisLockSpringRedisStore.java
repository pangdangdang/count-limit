package com.core;


import com.core.factory.CountLimitFacade;
import com.distributedproxylock.annotation.DistributedProxyLock;
import com.distributedproxylock.core.DistributedProxyLockCommonUtil;
import com.enums.CountFactoryEnum;
import com.util.CountLimitDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * SpringRedis锁SpringRedis存储
 *
 * @author tingmailang
 */
@Slf4j
@Component
public class SpringRedisLockSpringRedisStore extends CountLimitCommonBusiness implements CountLimitFacade<CountLimitDTO> {


    @Override
    public Boolean matching(CountFactoryEnum factory) {
        return Objects.equals(CountFactoryEnum.SPRING_REDIS_LOCK_SPRING_REDIS_STORE, factory);
    }

    @Override
    @DistributedProxyLock(key = CountLimitCommonUtil.COUNT_LIMIT_LOCK,
            suffixKeyTypeEnum = DistributedProxyLockCommonUtil.PARAM,
            objectName = "countLimitDTO",
            paramName = "LockKey",
            lockConnectionEnum = DistributedProxyLockCommonUtil.SPRING_REDIS)
    public boolean process(CountLimitDTO countLimitDTO) {
        if (countLimitDTO.getIsAdd()) {
            return super.springRedisCheckExceed(countLimitDTO.getKey(), countLimitDTO.getCount(), countLimitDTO.getLimit());
        } else {
            return super.springRedisReduce(countLimitDTO.getKey(), countLimitDTO.getCount());
        }
    }

}
