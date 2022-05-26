package com.core;

import com.distributedproxylock.annotation.DistributedProxyLock;
import com.distributedproxylock.enums.LockConnectionEnum;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 共用业务类
 */
@Service
public class CountLimitNode {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate redisTemplate;

    @DistributedProxyLock(key = CountLimitCommonUtil.COUNT_LIMIT_REDIS_NODE_LOCK)
    public void setNode(LockConnectionEnum lockConnectionEnum) {
        if (CountLimitCommonUtil.getNodeId() != null) {
            return;
        }
        switch (lockConnectionEnum) {
            case SPRING_REDIS:
                Integer temp = (Integer) redisTemplate.opsForValue().get(CountLimitCommonUtil.COUNT_LIMIT_REDIS_NODE_STORE);
                if (temp == null) {
                    temp = 0;
                }
                temp++;
                redisTemplate.opsForValue().set(CountLimitCommonUtil.COUNT_LIMIT_REDIS_NODE_STORE, temp);
                CountLimitCommonUtil.setNodeId(temp.toString());
                break;
            case REDISSON:
                RBucket<Integer> bucket = redissonClient.getBucket(CountLimitCommonUtil.COUNT_LIMIT_REDIS_NODE_STORE);
                Integer now = 0;
                if (bucket.isExists()) {
                    now = bucket.get() + 1;
                }
                bucket.set(now);
                CountLimitCommonUtil.setNodeId(now.toString());
                break;
        }
    }

}