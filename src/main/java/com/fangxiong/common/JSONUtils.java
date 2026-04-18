package com.fangxiong.common;



public class JSONUtils {
    public static String toJSONStr(Object o) {
        Class<?> c = o.getClass();
        return ParserFactory.decorateJSONStr(ParserFactory.getParser(c).parse(o, null));
    }
    public static <T> T jsonToBean(String jsonString, Class<T> clazz) {
        return (T) ConverterFactory.getConverter(clazz).convert(ConverterFactory.getUndecoratedJSONStr(jsonString), clazz);
    }


}
