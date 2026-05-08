package com.fangxiong.mysqlUtilsCore.exceptions;

public class MysqlConverterError extends RuntimeException {
    public MysqlConverterError(String message) {
        super(message);
    }
    public MysqlConverterError(String message, Throwable throwable){
      super(message,throwable);
    }
}
