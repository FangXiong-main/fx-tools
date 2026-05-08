package com.fangxiong.mysqlUtilsCore.coreUtil;

import com.fangxiong.globalUtils.GlobalConverterCacheLib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class CurrentFieldConverter {
    public static void convertCurrentField(ResultSet resultSet, Field field, Object convertingObj, Map<String, Type> typeCache, Map<String, Method> methodCache){
        Type type = typeCache.get(field.getName());
    }
}
