package com.fangxiong.jsonUtilsCore.exceptions;

public class JsonParserFailureError extends RuntimeException {
    public JsonParserFailureError(String message) {
        super(message);
    }
    public JsonParserFailureError(String message, Throwable cause) {super(message, cause);}
}
