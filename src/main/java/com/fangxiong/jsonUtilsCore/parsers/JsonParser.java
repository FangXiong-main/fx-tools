package com.fangxiong.jsonUtilsCore.parsers;

import java.lang.reflect.Field;

@FunctionalInterface
public interface JsonParser {
    String parse (Object o, Field f);
}
