package com.core.factory;


import com.enums.CountFactoryEnum;
import com.exception.CountLimitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CountLimitFacadeFactory<T> implements FactoryList<CountLimitFacade<T>, CountFactoryEnum> {
    @Autowired
    private List<CountLimitFacade> countLimitFacades;

    @Override
    public CountLimitFacade<T> getBean(CountFactoryEnum factory) {
        for (CountLimitFacade countLimitFacade : countLimitFacades) {
            if (countLimitFacade.matching(factory)) {
                return countLimitFacade;
            }
        }
        throw new CountLimitException("找不到计数限流实现类");
    }
}
