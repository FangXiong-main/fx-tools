package com.fangxiong.common;



public class JSONUtils {
    public static String BeanToJson(Object o) {
        Class<?> c = o.getClass();
        return ParserFactory.decorateJSONStr(ParserFactory.getParser(c).parse(o, null));
    }
    public static <T> T jsonToBean(String jsonString, Class<T> clazz) {
        return (T) NonGenericTypeConverterFactory.getConverter(clazz).convert(NonGenericTypeConverterFactory.getUndecoratedJSONStr(jsonString), clazz);
    }

    public static Object jsonToBean(String jsonString,CustomizeGenericTypes typeParams){
        return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) typeParams.getRawType()).convert(NonGenericTypeConverterFactory.getUndecoratedJSONStr(jsonString),typeParams);
    }

}
