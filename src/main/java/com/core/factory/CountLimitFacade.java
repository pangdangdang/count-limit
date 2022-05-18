package com.core.factory;


import com.enums.CountFactoryEnum;

/**
 * 计数限流  Facade
 *
 * @author tingmailang
 * @date 2020/3/2
 */
public interface CountLimitFacade<T> extends MatchingBean<CountFactoryEnum> {

    boolean process(T t);
}
