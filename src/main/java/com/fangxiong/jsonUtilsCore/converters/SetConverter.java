package com.fangxiong.jsonUtilsCore.converters;

import com.fangxiong.globalUtils.CustomizeGenericTypes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;

public class SetConverter implements GenericTypeJsonConverter {
    @Override
    public Object convert(String json, Type type) {
        ParameterizedType pt = (ParameterizedType) type;
        List<?> convertedList = (List<?>)GenericTypeConverterFactory.getGenericTypeJsonConverter(List.class).convert(json, new CustomizeGenericTypes(List.class,pt.getActualTypeArguments()));
        return new HashSet<>(convertedList);
    }
}
