package com.fangxiong.utils.json;


import com.fangxiong.common.CustomizeGenericTypes;
import com.fangxiong.common.GenericTypeConverterFactory;
import com.fangxiong.common.NonGenericTypeConverterFactory;
import com.fangxiong.common.ParserFactory;

public class JsonUtils {
    public static String BeanToJson(Object o) {
        Class<?> c = o.getClass();
        return ParserFactory.decorateJSONStr(ParserFactory.getParser(c).parse(o, null));
    }
    public static <T> T jsonToBean(String jsonString, Class<T> clazz) {
        String undecoratedJSONStr = StrUtils.getUndecoratedJSONStr(jsonString);
        StrUtils.jsonBracketMatchChecker(undecoratedJSONStr);
        StrUtils.jsonInvalidCharacterChecker(undecoratedJSONStr);
        return (T) NonGenericTypeConverterFactory.getConverter(clazz).convert(undecoratedJSONStr, clazz);
    }

    public static Object jsonToBean(String jsonString, CustomizeGenericTypes typeParams){
        return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) typeParams.getRawType()).convert(StrUtils.getUndecoratedJSONStr(jsonString),typeParams);
    }

}
