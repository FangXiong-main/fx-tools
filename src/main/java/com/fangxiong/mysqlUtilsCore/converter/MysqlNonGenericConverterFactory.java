package com.fangxiong.mysqlUtilsCore.converter;

import com.fangxiong.mysqlUtilsCore.exceptions.MysqlConverterError;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MysqlNonGenericConverterFactory {
    private static final Map<Class<?>,MysqlNonGenericConverter> mysqlNonGenericConverterMap = new HashMap<>();
    static{
        mysqlNonGenericConverterMap.put(String.class,(r,t,c)-> r.getString(c));
    }
}