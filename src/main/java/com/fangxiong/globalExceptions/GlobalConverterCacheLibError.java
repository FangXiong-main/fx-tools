package com.fangxiong.globalExceptions;

public class GlobalConverterCacheLibError extends RuntimeException {
    public GlobalConverterCacheLibError(String message) {
        super(message);
    }
    public GlobalConverterCacheLibError(String message,Throwable e){
        super(message,e);
    }
}
