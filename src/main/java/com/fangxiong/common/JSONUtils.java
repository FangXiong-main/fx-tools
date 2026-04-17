package com.fangxiong.common;

import com.fangxiong.common.parsers.ObjectParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONUtils {
    public static String toJSONStr(Object o) {
        Class<?> c = o.getClass();
        return ParserFactory.decorateJSONStr(ParserFactory.getParser(c).parse(o, null));
    }
    public static <T> T jsonToBean(String jsonString, Class<T> clazz) {
        T t;

        return null;
    }


}
