package com.fangxiong.jsonUtilsCore.converters;

import java.lang.reflect.Field;

@FunctionalInterface
public interface NonGenericTypeJsonConverter {
    Object convert(String s, Class<?> clazz, Field field);
}
