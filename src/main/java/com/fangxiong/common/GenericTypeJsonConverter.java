package com.fangxiong.common;

import com.fangxiong.common.converters.ObjectConverter;

import java.lang.reflect.Type;

public interface GenericTypeJsonConverter {
    Object convert(String json, Type type);
}
