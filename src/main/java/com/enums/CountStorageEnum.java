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
public enum CountStorageEnum {
    LOCAL_STORAGE("local_storage", "使用本地缓存"),
    REDISSON_STORAGE("redisson_storage", "使用Redisson存储"),
    SPRING_REDIS_STORAGE("spring_redis_storage", "使用spring_redis存储"),
    ;


    private final String code;
    private final String desc;

    CountStorageEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CountStorageEnum of(String key) {
        Optional<CountStorageEnum> assetStatusEnum = Arrays.stream(CountStorageEnum.values())
                .filter(c -> c.getCode().equals(key)).findFirst();
        return assetStatusEnum.orElse(null);
    }


}
