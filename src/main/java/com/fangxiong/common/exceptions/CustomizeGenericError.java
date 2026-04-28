package com.fangxiong.common.exceptions;

import java.lang.annotation.Retention;

public class CustomizeGenericError extends RuntimeException {
    public CustomizeGenericError(String message) {
        super(message);
    }
}
