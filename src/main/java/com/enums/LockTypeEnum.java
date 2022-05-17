package com.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author: tingmailang
 */
@Slf4j
@Getter
public enum LockTypeEnum {
    REENTRANT_LOCK("reentrant_lock", "使用ReentrantLock加锁"),
    REDISSON_LOCK("redisson_lock", "使用Redisson加锁"),
    SPRING_REDIS_LOCK("spring_redis_lock", "使用spring_redis加锁"),
    ;


    private final String code;
    private final String desc;

    LockTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static LockTypeEnum of(String key) {
        Optional<LockTypeEnum> assetStatusEnum = Arrays.stream(LockTypeEnum.values())
                .filter(c -> c.getCode().equals(key)).findFirst();
        return assetStatusEnum.orElse(null);
    }


}
