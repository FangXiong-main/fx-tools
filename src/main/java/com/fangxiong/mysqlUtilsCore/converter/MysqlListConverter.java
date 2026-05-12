package com.fangxiong.mysqlUtilsCore.converter;

import com.fangxiong.mysqlUtilsCore.exceptions.MysqlConverterError;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MysqlListConverter implements MysqlGenericConverter{
    @Override
    public Object converter(ResultSet resultSet, Type type) {
        List<Object> convertedList = new ArrayList<>();
        try {
            if(type instanceof ParameterizedType pt){
                if(pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1] instanceof ParameterizedType pt2){
                    if(pt2.getRawType() == Map.class){
                        while (resultSet.next()){
                            convertedList.add(MysqlGenericConverterFactory.getConverter(Map.class).converter(resultSet,pt2));
                        }
                    }else {
                        throw new MysqlConverterError("Return type not supported : List<" + pt2.getRawType().getTypeName()+">");
                    }
                }else {
                    return MysqlGenericConverterFactory.getConverter((Class<?>) pt.getRawType()).converter(resultSet,pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1]);
                }
            }else {
                while(resultSet.next()){
                    resultSet.previous();
                    convertedList.add(MysqlNonGenericConverterFactory.getConverter((Class<?>) type).converter(resultSet,(Class<?>) type,null));
                }
            }
        } catch (SQLException e) {
            throw new MysqlConverterError("convert list failed!",e);
        }
        return convertedList;
    }
}
