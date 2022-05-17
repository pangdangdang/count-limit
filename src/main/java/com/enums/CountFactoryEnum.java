package com.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 计算量处理工厂枚举
 *
 * @author tingmailang
 */
@Getter
public enum CountFactoryEnum {

    LOCAL_LOCK_STORAGE("local_lock_storage", "使用本地缓存"),
    REDISSON_STORAGE("redisson_storage", "使用Redisson存储"),
    SPRING_REDIS_STORAGE("spring_redis_storage", "使用spring_redis存储"),
    ;

    private String key;
    private String value;

    CountFactoryEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @JsonCreator
    public static CountFactoryEnum of(String key) {
        Optional<CountFactoryEnum> systemTypeEnum = Arrays.stream(CountFactoryEnum.values())
                .filter(c -> c.getKey().equals(key)).findFirst();
        return systemTypeEnum.orElse(null);
    }
}
