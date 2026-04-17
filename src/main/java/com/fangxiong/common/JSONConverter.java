package com.fangxiong.common;

import java.lang.reflect.Field;

@FunctionalInterface
public interface JSONConverter {
    Object convert(String s, Class<?> clazz);
}
