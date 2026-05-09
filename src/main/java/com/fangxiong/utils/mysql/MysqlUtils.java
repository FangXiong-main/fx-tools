package com.fangxiong.utils.mysql;

import com.fangxiong.mysqlUtilsCore.EnableUnderscoreToCamelCase;
import com.fangxiong.mysqlUtilsCore.converter.MysqlGenericConverterFactory;
import com.fangxiong.mysqlUtilsCore.exceptions.MysqlConverterError;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlUtils {

    private static EnableUnderscoreToCamelCase underscoreToCamelCaseEnum = EnableUnderscoreToCamelCase.DISABLE;

    public static <T> T getEntityFromResultSet(ResultSet resultSet,Class<?> clazz){
        try {
            return (T) MysqlGenericConverterFactory.getConverter(clazz).converter(resultSet,clazz,null);
        } catch (Exception e) {
            throw new MysqlConverterError("",e);
        }
    }

    public static <T> T getEntityFromResultSet(ResultSet resultSet,Class<?> clazz,EnableUnderscoreToCamelCase enableUnderscoreToCamelCase){
        if(enableUnderscoreToCamelCase == EnableUnderscoreToCamelCase.ENABLE){
            underscoreToCamelCaseEnum = EnableUnderscoreToCamelCase.ENABLE;
        }
        return (T) MysqlGenericConverterFactory.getConverter(clazz).converter(resultSet,clazz,null);
    }

    public static EnableUnderscoreToCamelCase underscoreToCamelCaseEnum(){
        return underscoreToCamelCaseEnum;
    }
}
