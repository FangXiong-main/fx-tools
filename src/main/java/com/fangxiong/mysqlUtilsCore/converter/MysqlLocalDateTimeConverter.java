package com.fangxiong.mysqlUtilsCore.converter;


import com.fangxiong.globalAnnotations.TimeType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MysqlLocalDateTimeConverter implements MysqlNonGenericConverter{
    @Override
    public Object converter(ResultSet resultSet, Class<?> clazz, String columName) throws SQLException {
        String timeStr;
        if (columName == null){
            timeStr = resultSet.getString(1);
        }else {
            timeStr = resultSet.getString(columName);
        }
        TimeType timeType = clazz.getDeclaredAnnotation(TimeType.class);
        if (timeType !=null){
            return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern(timeType.value()));
        }
        return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}
