package com.fangxiong.mysqlUtilsCore.converter;

import com.fangxiong.mysqlUtilsCore.coreUtil.CurrentColumnTypeDetector;
import com.fangxiong.mysqlUtilsCore.coreUtil.MysqlCoreUtils;
import com.fangxiong.mysqlUtilsCore.enums.EnableCamelCaseToUnderscore;
import com.fangxiong.mysqlUtilsCore.exceptions.MysqlConverterError;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MysqlMapConverter implements MysqlGenericConverter{

    private static CurrentColumnTypeDetector currentColumnTypeDetector = null;

    @Override
    public Object converter(ResultSet resultSet, Type type) {
        Map<Object,Object> convertedMap = new LinkedHashMap<>();
        try {
            if(type instanceof ParameterizedType pt){
                Type actualTypeArgument1 = pt.getActualTypeArguments()[0];
                Type actualTypeArgument2 = pt.getActualTypeArguments()[1];
                if(actualTypeArgument2 instanceof ParameterizedType pt2){
                    throw new MysqlConverterError("Unsupported return type : Map<"+pt2.getActualTypeArguments()[0].getTypeName()+","+pt2.getActualTypeArguments()[1].getTypeName()+">");
                }else {
                    if(actualTypeArgument1 == String.class){
                        if(currentColumnTypeDetector==null){
                            currentColumnTypeDetector = new CurrentColumnTypeDetector();
                        }
                        if(actualTypeArgument2 == Object.class){
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            int columnCount = metaData.getColumnCount();
                            for(int i =1 ;i<=columnCount;i++){
                                Class<?> columnType = currentColumnTypeDetector.getMysqlColumnTypeMap(metaData.getColumnType(i));
                                String columnName = metaData.getColumnName(i);
                                convertedMap.put(MysqlCoreUtils.convertColumnNameToCamelCase(columnName),MysqlNonGenericConverterFactory.getConverter(columnType).converter(resultSet,null,columnName));
                            }
                        }else {
                            throw new MysqlConverterError("Unsupported return type : Map<"+actualTypeArgument1.getTypeName()+","+actualTypeArgument2.getTypeName()+">.");
                        }
                    }else {
                        throw new MysqlConverterError("Unsupported return type : Map<"+actualTypeArgument1.getTypeName()+","+actualTypeArgument2.getTypeName()+">.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new MysqlConverterError("convert map failed!",e);
        }
        return convertedMap;
    }
}
