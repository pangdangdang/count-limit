package com.redislock.enums;

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
public enum RedisEnum {
    REDISSON("redisson", "使用redisson"),
    SPRING_REDIS("spring_redis", "使用springredis"),
    ;


    private final String code;
    private final String desc;

    RedisEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static RedisEnum of(String key) {
        Optional<RedisEnum> assetStatusEnum = Arrays.stream(RedisEnum.values())
                .filter(c -> c.getCode().equals(key)).findFirst();
        return assetStatusEnum.orElse(null);
    }


}
