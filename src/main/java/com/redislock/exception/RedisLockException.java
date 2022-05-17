package com.redislock.exception;

/**
 * Desc...
 *
 * @author Charley Wu
 * @date 2021/8/26
 */
public class RedisLockException extends RuntimeException {
  public RedisLockException() {
    super();
  }

  public RedisLockException(String message) {
    super("RedisLockException:" + message);
  }

  public RedisLockException(String message, Throwable cause) {
    super("RedisLockException:" + message, cause);
  }

  public RedisLockException(Throwable cause) {
    super(cause);
  }

}
