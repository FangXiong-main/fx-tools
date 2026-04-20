package com.fangxiong.common;

@FunctionalInterface
public interface NonGenericTypeJsonConverter {
    Object convert(String s, Class<?> clazz);
}
