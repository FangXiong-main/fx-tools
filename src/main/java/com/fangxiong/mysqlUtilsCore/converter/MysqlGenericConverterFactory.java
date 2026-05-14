package com.fangxiong.mysqlUtilsCore.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlGenericConverterFactory {
    private static final Map<Class<?>, MysqlGenericConverter> mysqlGenericConverterMap = new HashMap<>();

    static{
        mysqlGenericConverterMap.put(Map.class,new MysqlMapConverter());
        mysqlGenericConverterMap.put(List.class,new MysqlListConverter());
    }

    public static MysqlGenericConverter getConverter(Class<?> clazz){
        return mysqlGenericConverterMap.get(clazz);
    }
}
