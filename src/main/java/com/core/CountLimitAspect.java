package com.core;

import com.annotation.CountLimit;
import com.core.factory.CountLimitFacadeFactory;
import com.enums.CountFactoryEnum;
import com.exception.CountLimitException;
import com.util.CountLimitDTO;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class CountLimitAspect<T> {

    @Resource
    private CountLimitFacadeFactory<CountLimitDTO> countLimitFacadeFactory;

    @Pointcut("@annotation(com.annotation.CountLimit)")
    public void lockPointCut() {

    }

    @Around("lockPointCut() && @annotation(countLimit)")
    public Object around(ProceedingJoinPoint joinPoint, CountLimit countLimit) throws Throwable {
        LocalDateTime start = LocalDateTime.now();
        String inter = joinPoint.getTarget().getClass().getName() + joinPoint.getSignature().getName();
        String objectName = countLimit.objectName();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Map<String, Object> param = new HashMap<>();
        String par = countLimit.paramName();
        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], args[i]);
        }
        CountFactoryEnum countFactoryEnum = CountFactoryEnum.of(countLimit.countFactoryEnum());
        //获取限制的参数
        List<T> queryPar;
        String key = inter + objectName;
        int count;
        CountLimitDTO countLimitDTO = new CountLimitDTO();
        try {
            if (StringUtil.isBlank(par)) {
                //如果没有设置参数，说明在入参对象中
                queryPar = (List<T>) param.get(objectName);
            } else {
                //说明在入参的某个对象中，有一个参数是进行限流
                Object o = param.get(objectName);
                queryPar = (List<T>) CountLimitCommonUtil.getFieldValueByName(par, o);
                key += par;
            }
            count = queryPar.size();
            countLimitDTO.setKey(key);
            countLimitDTO.setCount(count);
            countLimitDTO.setLimit(countLimit.limit());
            countLimitDTO.setIsAdd(Boolean.TRUE);
            while (!countLimitFacadeFactory.getBean(countFactoryEnum).process(countLimitDTO)) {
                //是否超出等待时间
                if (start.plusSeconds(countLimit.waitTime()).isBefore(LocalDateTime.now())) {
                    throw new CountLimitException("超出等待时间" + key);
                }
                //将等待时长划分为等份时长
                Thread.sleep(countLimit.waitTime() * 1000 / CountLimitCommonUtil.SPILT_SLEEP);
            }
        } catch (Exception e) {
            throw new CountLimitException("计算限流异常", e);
        }

        try {
            return joinPoint.proceed();
        } finally {
            countLimitDTO.setIsAdd(Boolean.FALSE);
            boolean success = countLimitFacadeFactory.getBean(countFactoryEnum).process(countLimitDTO);
            if (!success) {
                throw new CountLimitException("计算量减少失败" + countLimitDTO.toString());
            }
        }
    }

}