package com.fangxiong.common.exceptions;

public class JsonInvalidValueError extends RuntimeException {
    public JsonInvalidValueError(String message) {
        super(message);
    }
}
