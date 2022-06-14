package com.core.factory;


import com.enums.CountFactoryEnum;
import com.exception.CountLimitException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class CountLimitFacadeFactory<T> implements FactoryList<CountLimitFacade<T>, CountFactoryEnum> {
    @Resource
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
