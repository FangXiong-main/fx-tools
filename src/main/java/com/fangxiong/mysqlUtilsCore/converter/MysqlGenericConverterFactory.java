package com.fangxiong.mysqlUtilsCore.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlGenericConverterFactory {
    private static final Map<Class<?>, MysqlGenericConverter> mysqlGenericConverterMap = new HashMap<>();

    static{
        mysqlGenericConverterMap.put(Map.class,null);
        mysqlGenericConverterMap.put(List.class,null);
    }

    public static MysqlGenericConverter getConverter(Class<?> clazz){
        return mysqlGenericConverterMap.get(clazz);
    }
}
