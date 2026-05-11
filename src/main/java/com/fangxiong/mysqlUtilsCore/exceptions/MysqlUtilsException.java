package com.fangxiong.mysqlUtilsCore.exceptions;

public class MysqlUtilsException extends RuntimeException {
  public MysqlUtilsException(String message) {
    super(message);
  }
  public MysqlUtilsException(String message,Throwable throwable){
    super(message,throwable);
  }
}
