package com.fangxiong.jsonUtilsCore.exceptions;

public class JsonInvalidValueError extends RuntimeException {
    public JsonInvalidValueError(String message) {
        super(message);
    }
    public JsonInvalidValueError(String message, Throwable cause) {
        super(message, cause);
    }
}
