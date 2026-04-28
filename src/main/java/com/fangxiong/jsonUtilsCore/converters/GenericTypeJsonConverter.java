package com.fangxiong.jsonUtilsCore.converters;

import java.lang.reflect.Type;

public interface GenericTypeJsonConverter {
    Object convert(String json, Type type);
}
