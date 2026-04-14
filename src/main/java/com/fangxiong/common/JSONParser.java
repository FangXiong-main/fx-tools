package com.fangxiong.common;

import java.lang.reflect.Field;

@FunctionalInterface
public interface JSONParser {
    String parse (Object o, Field f);
}
