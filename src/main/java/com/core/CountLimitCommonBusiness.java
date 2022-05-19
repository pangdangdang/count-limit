package com.core;

import com.redislock.annotation.RedisLock;
import com.redislock.core.RedisLockCommonUtil;
import com.redislock.enums.RedisEnum;
import com.util.CountLimitDTO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

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

    @Resource
    private CountLimitNode countLimitNode;

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
        this.checkNode(RedisEnum.REDISSON);
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
        this.checkNode(RedisEnum.REDISSON);
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
        int now = countMap.getOrDefault(key, 0);
        if (now + count > limit) {
            return false;
        } else {
            log.debug("CountLimitAspect:{}增加计算量:{}", key, now + count);
            countMap.put(key, now + count);
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
        int now = countMap.get(key);
        log.debug("CountLimitAspect:{}减少计算量:{}", key, now - count);
        countMap.put(key, now - count);
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
        this.checkNode(RedisEnum.SPRING_REDIS);
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
        this.checkNode(RedisEnum.SPRING_REDIS);
        int now = (int) redisTemplate.opsForValue().get(CountLimitCommonUtil.COUNT_LIMIT_STORE + CountLimitCommonUtil.getNodeId() + key);
        redisTemplate.opsForValue().set(CountLimitCommonUtil.COUNT_LIMIT_STORE + CountLimitCommonUtil.getNodeId() + key, now - count);
        return true;
    }

    /**
     * 检查节点id，如果为空就设置
     * @param redisEnum
     */
    public void checkNode(RedisEnum redisEnum) {
        if (CountLimitCommonUtil.getNodeId() != null) {
            return;
        }
        countLimitNode.setNode(redisEnum);
    }
}