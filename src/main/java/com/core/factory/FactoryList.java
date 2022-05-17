package com.core.factory;

public interface FactoryList<E extends MatchingBean<K>, K> {

    E getBean(K factory);

}
