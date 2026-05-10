package com.fangxiong.utils.mysql;

import com.fangxiong.mysqlUtilsCore.converter.MysqlNonGenericConverterFactory;
import com.fangxiong.mysqlUtilsCore.enums.EnableCamelCaseToUnderscore;
import com.fangxiong.mysqlUtilsCore.enums.EnableUnderscoreToCamelCase;
import com.fangxiong.mysqlUtilsCore.converter.MysqlGenericConverterFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlUtils {



    public static <T> T getEntityFromResultSet(ResultSet resultSet,Class<T> clazz){
        try {
            return (T) MysqlGenericConverterFactory.getConverter(clazz).converter(resultSet,null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getEntityFromResultSet(ResultSet resultSet,Class<T> clazz,EnableCamelCaseToUnderscore enableCamelCaseToUnderscore){
        if(enableCamelCaseToUnderscore == EnableCamelCaseToUnderscore.ENABLE){
            MysqlNonGenericConverterFactory.setCamelCaseToUnderscoreEnumStatus(EnableCamelCaseToUnderscore.ENABLE);
        }
        try {
            return (T) MysqlNonGenericConverterFactory.getConverter(clazz).converter(resultSet,clazz,null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
