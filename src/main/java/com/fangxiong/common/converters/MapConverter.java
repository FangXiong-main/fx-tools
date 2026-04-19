package com.fangxiong.common.converters;

import com.fangxiong.common.ConverterFactory;
import com.fangxiong.common.JSONConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapConverter implements JSONConverter {

    private static final Pattern isIntegerPattern = Pattern.compile("-?(\\d+)");
    private static final Pattern isDicimalPattern = Pattern.compile("-?(\\d+\\.\\d+)");
    private static final ThreadLocal<Type> convertingRawTypeCache = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Type[]> convertingActualTypeCache = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Boolean> isFirst = ThreadLocal.withInitial(() -> true);
    private static final ThreadLocal<Boolean> isConverting = ThreadLocal.withInitial(() -> false);

    @Override
    public Object convert(String s, Class<?> clazz) {
        Map<String,Object> map = new LinkedHashMap<>();
        Map<String,String> mapKeysAndValues = getJSONKeysAndValues(s);
        StringBuilder sbMain = new StringBuilder();
        if(clazz==null){
            if(isFirst.get()){
                convertingRawTypeCache.set(ObjectConverter.pollConvertRawTypesDeque());
                convertingActualTypeCache.set(ObjectConverter.pollConvertActualTypesDeque());
                isFirst.set(false);
            }
            if(convertingActualTypeCache.get()!=null && convertingActualTypeCache.get()[1] instanceof ParameterizedType pt){
                Map<String, String> splitMainEntityAndFieldEntity = ConverterFactory.getSplitMainEntityAndFieldEntity(sbMain, s);
                splitMainEntityAndFieldEntity =  splitMainEntityAndFieldEntity.isEmpty() ? getJSONKeysAndValues(sbMain.toString()) : splitMainEntityAndFieldEntity ;
                convertingRawTypeCache.set(ObjectConverter.pollConvertRawTypesDeque());
                convertingActualTypeCache.set(ObjectConverter.pollConvertActualTypesDeque());
                for (String key : splitMainEntityAndFieldEntity.keySet()) {
                    isConverting.set(true);
                    map.put(key,ConverterFactory.getConverter((Class<?>) pt.getRawType()).convert(splitMainEntityAndFieldEntity.get(key),null));
                }
                isConverting.set(false);
            }else{
                return ConverterFactory.getConverter((Class<?>) convertingRawTypeCache.get()).convert(s, (Class<?>) convertingActualTypeCache.get()[1]);
            }
        }else if (clazz == Map.class){
            getJSONKeysAndValues(s);
            System.out.println(mapKeysAndValues);
            for (String key:mapKeysAndValues.keySet()){
                map.put("\""+key+"\"",getObjectValue(mapKeysAndValues.get(key)));
            }
        }else{
            getJSONKeysAndValues(s);
            for (String key:mapKeysAndValues.keySet()){
                map.put(key,ConverterFactory.getConverter(clazz).convert(mapKeysAndValues.get(key),clazz));
            }
        }
        return map;
    }

    private static Map<String,String> getJSONKeysAndValues(String s) {
        Map<String,String> mapKeysAndValues = new LinkedHashMap<>();
        StringBuilder sbKeys = new StringBuilder();
        StringBuilder sbValues = new StringBuilder();
        char[] ca = s.toCharArray();
        int quotationMarksCount = 0;
        boolean isReadingKey = false;boolean isReadingValue = false;
        boolean readValueFinished=false;boolean readKeyFinished=false;boolean valueIsNotString=false;
        for (int i = 0; i < ca.length; i++) {
            if(i<=ca.length-2&&ca[i+1]!='\"'&&ca[i]==':') {
                isReadingValue=true;isReadingKey=false;readKeyFinished=true;valueIsNotString=true;
            } else if (i<=ca.length-2&&ca[i+1]=='\"'&&ca[i]==':'){
                isReadingValue=true;isReadingKey=false;readKeyFinished=true;
            } else if (i>=1 && ca[i]=='\"' && !isReadingKey && (ca[i-1]==':'||ca[i-1]=='{'||ca[i-1]==',') && !valueIsNotString) {
                quotationMarksCount++;
            } else if (valueIsNotString && isReadingValue && (ca[i]==','||ca[i]=='}')) {
                readValueFinished=true;isReadingValue=false;valueIsNotString=false;i--;
            } else if (ca[i]=='\"' && (isReadingKey||isReadingValue)) {
                quotationMarksCount--;
            } else if (quotationMarksCount == 1 && !isReadingKey && !isReadingValue) {
                sbKeys.append(ca[i]);
                isReadingKey = true;
            } else if (quotationMarksCount == 0 && readKeyFinished && readValueFinished) {
                mapKeysAndValues.put(sbKeys.toString(),sbValues.toString());
                sbKeys.setLength(0);sbValues.setLength(0);
                isReadingKey = false;isReadingValue = false;
                readKeyFinished = false;readValueFinished = false;
                valueIsNotString = false;
            } else if (quotationMarksCount == 0 && isReadingKey) {
                isReadingKey = false;
                readKeyFinished = true;
            } else if (quotationMarksCount == 0 && isReadingValue && !valueIsNotString) {
                isReadingValue = false;
                readValueFinished = true;
                i--;
            } else if (isReadingKey) {
                sbKeys.append(ca[i]);
            } else if (isReadingValue) {
                sbValues.append(ca[i]);
            }
        }
        return mapKeysAndValues;
    }

    private static Object getObjectValue(String value){
        Matcher matcher = isIntegerPattern.matcher(value);
        if (matcher.find()){
            return Integer.parseInt(matcher.group(1));
        }
        Matcher matcher2 = isDicimalPattern.matcher(value);
        if (matcher2.find()){
            return Double.parseDouble(matcher2.group(1));
        }
        if(value.equals("null")){
            return "null";
        }
        return "\""+value+"\"";
    }

    public static void removeMapConverterLocalThreadCache() {
        convertingRawTypeCache.remove();
        convertingActualTypeCache.remove();
    }
}
