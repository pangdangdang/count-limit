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
 * redisson锁redisson存储
 *
 * @author tingmailang
 */
@Slf4j
@Component
public class RedissonLockRedissonStore extends CountLimitCommonBusiness implements CountLimitFacade<CountLimitDTO> {

    @Override
    public Boolean matching(CountFactoryEnum factory) {
        return Objects.equals(CountFactoryEnum.REDISSON_LOCK_REDISSON_STORE, factory);
    }

    @Override
    @DistributedProxyLock(key = CountLimitCommonUtil.COUNT_LIMIT_LOCK,
            suffixKeyTypeEnum = DistributedProxyLockCommonUtil.PARAM,
            objectName = "countLimitDTO",
            paramName = "LockKey")
    public boolean process(CountLimitDTO countLimitDTO) {
        if (countLimitDTO.getIsAdd()) {
            return super.redissonCheckExceed(countLimitDTO.getKey(), countLimitDTO.getCount(), countLimitDTO.getLimit());
        } else {
            return super.redissonReduce(countLimitDTO.getKey(), countLimitDTO.getCount());
        }
    }

}
