package com.fangxiong.common.exceptions;

public class JsonSyntaxError extends RuntimeException {
    public JsonSyntaxError(String message) {
        super(message);
    }
}
