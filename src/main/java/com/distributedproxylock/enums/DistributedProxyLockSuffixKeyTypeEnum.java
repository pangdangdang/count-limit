package com.distributedproxylock.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author: tingmailang
 */
@Getter
public enum DistributedProxyLockSuffixKeyTypeEnum {
    NO_SUFFIX("no_suffix", "没有后缀"),
    THREAD_LOCAL("thread_local", "通过ThreadLocal获取后缀"),
    PARAM("param", "通过参数获取后缀"),
    ;


    private final String code;
    private final String desc;

    DistributedProxyLockSuffixKeyTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistributedProxyLockSuffixKeyTypeEnum of(String key) {
        Optional<DistributedProxyLockSuffixKeyTypeEnum> assetStatusEnum = Arrays.stream(DistributedProxyLockSuffixKeyTypeEnum.values())
                .filter(c -> c.getCode().equals(key)).findFirst();
        return assetStatusEnum.orElse(null);
    }


}
