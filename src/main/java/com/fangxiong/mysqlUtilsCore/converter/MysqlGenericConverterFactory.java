package com.fangxiong.mysqlUtilsCore.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlGenericConverterFactory {
    private static final Map<Class<?>,MysqlConverter> mysqlConverterMap = new HashMap<>();

    static{
        mysqlConverterMap.put(Map.class,null);
        mysqlConverterMap.put(List.class,null);
    }

    private static MysqlConverter addConverter(Class<?> clazz){
        MysqlConverter mysqlConverter = new MysqlObjectConverter();
        mysqlConverterMap.put(clazz,mysqlConverter);
        return mysqlConverter;
    }

    public static MysqlConverter getConverter(Class<?> clazz){
        MysqlConverter mysqlConverter = mysqlConverterMap.get(clazz);
        if(mysqlConverter == null){
            return addConverter(clazz);
        }
        return mysqlConverter;
    }
}
