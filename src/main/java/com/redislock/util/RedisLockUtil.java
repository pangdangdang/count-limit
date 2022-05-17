package com.redislock.util;


/**
 * 锁后缀
 *
 * @author tingmailang
 */
public class RedisLockUtil {


    static ThreadLocal<String> LOCK_KEY = new ThreadLocal<String>();

    public static void set(String key) {
        LOCK_KEY.set(key);
    }
    public static String get() {
        return LOCK_KEY.get();
    }
    public static void remove() {
        LOCK_KEY.remove();
    }
}
