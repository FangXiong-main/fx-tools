package com.fangxiong.jsonUtilsCore.converters;

@FunctionalInterface
public interface NonGenericTypeJsonConverter {
    Object convert(String s, Class<?> clazz);
}
