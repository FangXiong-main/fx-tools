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
import java.util.HashMap;
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
                            Map<Integer,String> convertedColumnNameCache = getConvertedColumnNameCache(metaData,columnCount);
                            for(int i =1 ;i<=columnCount;i++){
                                Class<?> columnType = currentColumnTypeDetector.getMysqlColumnTypeMap(metaData.getColumnType(i));
                                convertedMap.put(convertedColumnNameCache.get(i),MysqlNonGenericConverterFactory.getConverter(columnType).converter(resultSet,null,metaData.getColumnName(i)));
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

    private static Map<Integer,String> getConvertedColumnNameCache(ResultSetMetaData metaData,int columnCount){
        Map<Integer,String> cacheMap = new HashMap<>();int tempI=0;
        try {
            if (MysqlNonGenericConverterFactory.getCamelCaseToUnderscoreStatus() == EnableCamelCaseToUnderscore.ENABLE){
                for(int i=1 ;i<=columnCount;i++){
                    tempI=i;
                    cacheMap.put(i,MysqlCoreUtils.convertColumnNameToCamelCase(metaData.getColumnName(i)));
                }
            }else {
                for(int i=1 ;i<=columnCount;i++){
                    tempI=i;
                    cacheMap.put(i,metaData.getColumnName(i));
                }
            }
        } catch (SQLException e) {
            throw new MysqlConverterError("Can't get the column name with the column index :"+tempI+".");
        }
        return cacheMap;
    }

}
