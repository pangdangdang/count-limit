package com.core.factory;

/**
 * @Author: tingmailang
 * @Description: 匹配工厂类
 */
public interface MatchingBean<T> {
    Boolean matching(T factory);
}
