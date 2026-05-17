package com.fangxiong.mysqlUtilsCore.exceptions;

import com.fangxiong.mysqlUtilsCore.coreUtil.MysqlCoreUtils;

public class MysqlUtilsException extends RuntimeException {
  public MysqlUtilsException(String message) {
    super(message);
  }
  public MysqlUtilsException(String message,Throwable throwable){
    super(message,throwable);
  }
  public MysqlUtilsException(Throwable e){
    super(e);
  }
}
