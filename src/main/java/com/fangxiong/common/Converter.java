package com.fangxiong.common;

@FunctionalInterface
public interface Converter {
    Object convert(String jsonString);
}
