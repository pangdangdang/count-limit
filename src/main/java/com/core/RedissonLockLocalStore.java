package com.core;


import com.core.factory.CountLimitFacade;
import com.enums.CountFactoryEnum;
import com.redislock.annotation.RedisLock;
import com.util.CountLimitDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redisson锁本地存储
 *
 * @author tingmailang
 */
@Slf4j
@Component
public class RedissonLockLocalStore extends CountLimitCommonBusiness implements CountLimitFacade<CountLimitDTO> {

    private static volatile ConcurrentHashMap<String, Integer> countMap = new ConcurrentHashMap<>();

    @Override
    public Boolean matching(CountFactoryEnum factory) {
        return Objects.equals(CountFactoryEnum.REDISSON_LOCK_STORE, factory);
    }

    @Override
    @RedisLock(key = CountLimitCommonUtil.COUNT_LIMIT_LOCK,
            suffixKeyTypeEnum = "param",
            objectName = "countLimitDTO",
            paramName = "LockKey")
    public boolean process(CountLimitDTO countLimitDTO) {
        if (countLimitDTO.getIsAdd()) {
            return super.localCheckExceed(countLimitDTO.getKey(), countLimitDTO.getCount(), countLimitDTO.getLimit());
        } else {
            return super.localReduce(countLimitDTO.getKey(), countLimitDTO.getCount());
        }
    }

}
