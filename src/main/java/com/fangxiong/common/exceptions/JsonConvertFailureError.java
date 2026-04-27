package com.fangxiong.common.exceptions;

public class JsonConvertFailureError extends RuntimeException {
    public JsonConvertFailureError(String message,Throwable t) {
        super(message,t);
    }

    public JsonConvertFailureError(String message){super(message);}
}
