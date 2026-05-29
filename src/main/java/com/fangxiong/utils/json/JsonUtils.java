package com.fangxiong.utils.json;


import com.fangxiong.jsonUtilsCore.coreUtil.JsonOperationUtil;
import com.fangxiong.jsonUtilsCore.coreUtil.CustomizeGenericTypes;
import com.fangxiong.jsonUtilsCore.converters.GenericTypeConverterFactory;
import com.fangxiong.jsonUtilsCore.converters.NonGenericTypeConverterFactory;
import com.fangxiong.jsonUtilsCore.enums.DecorateJson;
import com.fangxiong.jsonUtilsCore.enums.SafetyCheckLevel;
import com.fangxiong.jsonUtilsCore.parsers.ParserFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {
    private static final Pattern stringPattern = Pattern.compile("\"(\\S+)\"");
    public static String beanToJson(Object o) {
        if(o instanceof String){
            Matcher matcher = stringPattern.matcher(o.toString());
            if (matcher.find()){
                return matcher.group(1);
            }else {
                return (String) o;
            }
        }
        return ParserFactory.getParser(o.getClass()).parse(o, null);
    }

    public static String beanToJson(Object o, DecorateJson decorateJson) {
        Class<?> c = o.getClass();
        if(decorateJson == DecorateJson.YES){
            return ParserFactory.decorateJSONStr(ParserFactory.getParser(c).parse(o, null));
        }
        return ParserFactory.getParser(c).parse(o, null);
    }

    public static <T> T jsonToBean(String jsonString, Class<T> clazz) {
        String undecoratedJSONStr = JsonOperationUtil.getUndecoratedJSONStr(jsonString);
        JsonOperationUtil.jsonBracketMatchChecker(undecoratedJSONStr);
        JsonOperationUtil.jsonInvalidCharacterChecker(undecoratedJSONStr);
        return (T) NonGenericTypeConverterFactory.getConverter(clazz).convert(undecoratedJSONStr, clazz,null);
    }

    public static <T> T jsonToBean(String jsonString, Class<T> clazz, SafetyCheckLevel safetyCheckLevel) {
        String undecoratedJSONStr = JsonOperationUtil.getUndecoratedJSONStr(jsonString);
        if(safetyCheckLevel == SafetyCheckLevel.NORMAL) {
            JsonOperationUtil.jsonBracketMatchChecker(undecoratedJSONStr);
            JsonOperationUtil.jsonInvalidCharacterChecker(undecoratedJSONStr);
        } else if (safetyCheckLevel == SafetyCheckLevel.FAST) {
            JsonOperationUtil.jsonInvalidCharacterChecker(undecoratedJSONStr);
        }
        return (T) NonGenericTypeConverterFactory.getConverter(clazz).convert(undecoratedJSONStr, clazz,null);
    }


    public static Object jsonToBean(String jsonString, CustomizeGenericTypes typeParams){
        String undecoratedJSONStr = JsonOperationUtil.getUndecoratedJSONStr(jsonString);
        JsonOperationUtil.jsonBracketMatchChecker(undecoratedJSONStr);
        JsonOperationUtil.jsonInvalidCharacterChecker(undecoratedJSONStr);
        return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) typeParams.getRawType()).convert(undecoratedJSONStr,typeParams);
    }

}
