package com.exception;

/**
 * Desc...
 *
 * @author tingmailang
 */
public class CountLimitException extends RuntimeException {
  public CountLimitException() {
    super();
  }

  public CountLimitException(String message) {
    super("CountLimitException:" + message);
  }

  public CountLimitException(String message, Throwable cause) {
    super("CountLimitException:" + message, cause);
  }

  public CountLimitException(Throwable cause) {
    super(cause);
  }

}
