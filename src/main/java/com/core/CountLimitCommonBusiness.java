package com.core;

import com.exception.CountLimitException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 共用业务类
 */
@Service
@Slf4j
public class CountLimitCommonBusiness {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate redisTemplate;

    private static volatile ConcurrentHashMap<String, Integer> countMap = new ConcurrentHashMap<>();

    /**
     * redisson检查是否超出计算限制
     *
     * @param key
     * @param count
     * @param limit
     * @return
     */
    public boolean redissonCheckExceed(String key, int count, int limit) {
        RBucket<Integer> bucket = redissonClient.getBucket(CountLimitCommonUtil.COUNT_LIMIT_STORE + CountLimitCommonUtil.getNodeId() + key);
        int now = 0;
        if (bucket.isExists()) {
            now = bucket.get();
        }
        if (now + count > limit) {
            return false;
        } else {
            bucket.set(now + count);
            return true;
        }
    }


    /**
     * redisson减少目前在查询中的参数量级
     *
     * @param key
     * @param count
     */
    public boolean redissonReduce(String key, int count) {
        RBucket<Integer> bucket = redissonClient.getBucket(CountLimitCommonUtil.COUNT_LIMIT_STORE + CountLimitCommonUtil.getNodeId() + key);
        int now = bucket.get();
        bucket.set(now - count);
        return true;
    }


    /**
     * local检查是否超出计算限制
     *
     * @param key
     * @param count
     * @param limit
     * @return
     */
    public boolean localCheckExceed(String key, int count, int limit) {
        int now = countMap.getOrDefault(CountLimitCommonUtil.getNodeId() + key, 0);
        if (now + count > limit) {
            return false;
        } else {
            log.debug("CountLimitAspect:{}增加计算量:{}", CountLimitCommonUtil.getNodeId() + key, now + count);
            countMap.put(CountLimitCommonUtil.getNodeId() + key, now + count);
            return true;
        }
    }


    /**
     * local减少目前在查询中的参数量级
     *
     * @param key
     * @param count
     */
    public boolean localReduce(String key, int count) {
        int now = countMap.get(CountLimitCommonUtil.getNodeId() + key);
        log.debug("CountLimitAspect:{}减少计算量:{}", CountLimitCommonUtil.getNodeId() + key, now - count);
        countMap.put(CountLimitCommonUtil.getNodeId() + key, now - count);
        return true;
    }


    /**
     * springRedis检查是否超出计算限制
     *
     * @param key
     * @param count
     * @param limit
     * @return
     */
    public boolean springRedisCheckExceed(String key, int count, int limit) {
        int now = (int) redisTemplate.opsForValue().get(CountLimitCommonUtil.COUNT_LIMIT_STORE + CountLimitCommonUtil.getNodeId() + key);
        if (now + count > limit) {
            return false;
        } else {
            redisTemplate.opsForValue().set(CountLimitCommonUtil.COUNT_LIMIT_STORE + CountLimitCommonUtil.getNodeId() + key, now + count);
            return true;
        }
    }


    /**
     * springRedis减少目前在查询中的参数量级
     *
     * @param key
     * @param count
     */
    public boolean springRedisReduce(String key, int count) {
        int now = (int) redisTemplate.opsForValue().get(CountLimitCommonUtil.COUNT_LIMIT_STORE + CountLimitCommonUtil.getNodeId() + key);
        redisTemplate.opsForValue().set(CountLimitCommonUtil.COUNT_LIMIT_STORE + CountLimitCommonUtil.getNodeId() + key, now - count);
        return true;
    }

}