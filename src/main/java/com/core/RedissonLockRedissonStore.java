package com.core;


import com.core.factory.CountLimitFacade;
import com.enums.CountFactoryEnum;
import com.redislock.annotation.RedisLock;
import com.util.CountLimitDTO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * redisson锁redisson存储
 *
 * @author tingmailang
 */
@Slf4j
@Component
public class RedissonLockRedissonStore extends CountLimitCommonBusiness implements CountLimitFacade<CountLimitDTO> {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public Boolean matching(CountFactoryEnum factory) {
        return Objects.equals(CountFactoryEnum.REDISSON_LOCK_REDISSON_STORE, factory);
    }

    @Override
    @RedisLock(key = CountLimitCommonUtil.COUNT_LIMIT_LOCK,
            suffixKeyTypeEnum = "param",
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
