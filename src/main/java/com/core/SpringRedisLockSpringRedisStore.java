package com.core;


import com.core.factory.CountLimitFacade;
import com.enums.CountFactoryEnum;
import com.redislock.annotation.RedisLock;
import com.redislock.core.RedisLockCommonUtil;
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
    @RedisLock(key = CountLimitCommonUtil.COUNT_LIMIT_LOCK,
            suffixKeyTypeEnum = RedisLockCommonUtil.PARAM,
            objectName = "countLimitDTO",
            paramName = "LockKey",
            redisEnum = RedisLockCommonUtil.SPRING_REDIS)
    public boolean process(CountLimitDTO countLimitDTO) {
        if (countLimitDTO.getIsAdd()) {
            return super.springRedisCheckExceed(countLimitDTO.getKey(), countLimitDTO.getCount(), countLimitDTO.getLimit());
        } else {
            return super.springRedisReduce(countLimitDTO.getKey(), countLimitDTO.getCount());
        }
    }

}
