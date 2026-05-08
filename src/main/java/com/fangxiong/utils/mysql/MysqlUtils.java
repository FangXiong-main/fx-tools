package com.fangxiong.utils.mysql;

import com.fangxiong.mysqlUtilsCore.EnableUnderscoreToCamelCase;
import com.fangxiong.mysqlUtilsCore.converter.MysqlGenericConverterFactory;

import java.sql.ResultSet;

public class MysqlUtils {

    private static EnableUnderscoreToCamelCase underscoreToCamelCaseEnum = EnableUnderscoreToCamelCase.DISABLE;

    public static <T> T getEntityFromResultSet(ResultSet resultSet,Class<?> clazz){
        return (T) MysqlGenericConverterFactory.getConverter(clazz).converter(resultSet,clazz);
    }

    public static <T> T getEntityFromResultSet(ResultSet resultSet,Class<?> clazz,EnableUnderscoreToCamelCase enableUnderscoreToCamelCase){
        if(enableUnderscoreToCamelCase == EnableUnderscoreToCamelCase.ENABLE){
            underscoreToCamelCaseEnum = EnableUnderscoreToCamelCase.ENABLE;
        }
        return (T) MysqlGenericConverterFactory.getConverter(clazz).converter(resultSet,clazz);
    }

    public static EnableUnderscoreToCamelCase underscoreToCamelCaseEnum(){
        return underscoreToCamelCaseEnum;
    }
}
