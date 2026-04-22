package com.fangxiong.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class CustomizeGenericTypes implements ParameterizedType {
    private final Type rawType;
    private final Type[] actualTypes;

    public CustomizeGenericTypes(Type rawType,Type... actualTypes){
        this.rawType = rawType;
        this.actualTypes = actualTypes;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypes;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
