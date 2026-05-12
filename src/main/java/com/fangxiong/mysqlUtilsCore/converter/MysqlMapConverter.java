package com.fangxiong.mysqlUtilsCore.converter;

import com.fangxiong.mysqlUtilsCore.exceptions.MysqlConverterError;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MysqlMapConverter implements MysqlGenericConverter{
    @Override
    public Object converter(ResultSet resultSet, Type type) {
        Map<Object,Object> convertedMap = new HashMap<>();
        try {
            if(type instanceof ParameterizedType pt){
                Type rawType = pt.getRawType();
                Type actualTypeArgument = pt.getActualTypeArguments()[pt.getActualTypeArguments().length - 1];
                if(actualTypeArgument instanceof ParameterizedType pt2){
                    throw new MysqlConverterError("Unsupported return type : Map<"+pt2.getRawType().getTypeName()+">");
                }else {
                    if(rawType == Integer.class){
                        while (resultSet.next()){

                        }
                    }else if (rawType == String.class){

                    }
                }
            }
        } catch (SQLException e) {
            throw new MysqlConverterError("convert map failed!",e);
        }
        return null;
    }
}
