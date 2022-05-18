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

    LOCAL_LOCK_STORE("local_lock_store", "使用本地锁本地缓存"),
    LOCAL_LOCK_REDISSON_STORE("local_lock_redisson_store", "使用本地锁redisson缓存"),
    LOCAL_LOCK_SPRING_REDIS_STORE("local_lock_spring_redis_store", "使用本地锁spring redis缓存"),
    REDISSON_LOCK_STORE("redisson_lock_store", "使用redisson锁本地缓存"),
    REDISSON_LOCK_REDISSON_STORE("redisson_lock_redisson_store", "使用redisson锁redisson缓存"),
    SPRING_REDIS_LOCK_STORE("spring_redis_lock_store", "使用spring redis锁本地缓存"),
    SPRING_REDIS_LOCK_SPRING_REDIS_STORE("spring_redis_lock_spring_redis_store", "使用spring redis锁spring redis缓存"),
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
