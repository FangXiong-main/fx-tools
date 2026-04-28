package com.fangxiong.utils.json;


import com.fangxiong.jsonUtilsCore.basicJsonOperation.JsonOperationFactory;
import com.fangxiong.jsonUtilsCore.customize.CustomizeGenericTypes;
import com.fangxiong.jsonUtilsCore.converters.GenericTypeConverterFactory;
import com.fangxiong.jsonUtilsCore.converters.NonGenericTypeConverterFactory;
import com.fangxiong.jsonUtilsCore.enums.SafetyCheckLevel;
import com.fangxiong.jsonUtilsCore.parsers.ParserFactory;

public class JsonUtils {

    public static String BeanToJson(Object o) {
        Class<?> c = o.getClass();
        return ParserFactory.decorateJSONStr(ParserFactory.getParser(c).parse(o, null));
    }

    public static <T> T jsonToBean(String jsonString, Class<T> clazz) {
        String undecoratedJSONStr = JsonOperationFactory.getUndecoratedJSONStr(jsonString);
        JsonOperationFactory.jsonBracketMatchChecker(undecoratedJSONStr);
        JsonOperationFactory.jsonInvalidCharacterChecker(undecoratedJSONStr);
        return (T) NonGenericTypeConverterFactory.getConverter(clazz).convert(undecoratedJSONStr, clazz);
    }

    public static <T> T jsonToBean(String jsonString, Class<T> clazz, SafetyCheckLevel safetyCheckLevel) {
        String undecoratedJSONStr = JsonOperationFactory.getUndecoratedJSONStr(jsonString);
        if(safetyCheckLevel == SafetyCheckLevel.NORMAL) {
            JsonOperationFactory.jsonBracketMatchChecker(undecoratedJSONStr);
            JsonOperationFactory.jsonInvalidCharacterChecker(undecoratedJSONStr);
        } else if (safetyCheckLevel == SafetyCheckLevel.FAST) {
            JsonOperationFactory.jsonInvalidCharacterChecker(undecoratedJSONStr);
        }
        return (T) NonGenericTypeConverterFactory.getConverter(clazz).convert(undecoratedJSONStr, clazz);
    }

    public static Object jsonToBean(String jsonString, CustomizeGenericTypes typeParams){
        String undecoratedJSONStr = JsonOperationFactory.getUndecoratedJSONStr(jsonString);
        JsonOperationFactory.jsonBracketMatchChecker(undecoratedJSONStr);
        JsonOperationFactory.jsonInvalidCharacterChecker(undecoratedJSONStr);
        return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) typeParams.getRawType()).convert(undecoratedJSONStr,typeParams);
    }

}
