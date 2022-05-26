package com.distributedproxylock.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author: tingmailang
 */
@Getter
public enum LockConnectionEnum {
    REDISSON("redisson", "使用redisson"),
    SPRING_REDIS("spring_redis", "使用springredis"),
    ;


    private final String code;
    private final String desc;

    LockConnectionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static LockConnectionEnum of(String key) {
        Optional<LockConnectionEnum> assetStatusEnum = Arrays.stream(LockConnectionEnum.values())
                .filter(c -> c.getCode().equals(key)).findFirst();
        return assetStatusEnum.orElse(null);
    }


}
