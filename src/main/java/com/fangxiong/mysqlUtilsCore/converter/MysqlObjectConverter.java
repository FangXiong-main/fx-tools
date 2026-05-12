package com.fangxiong.mysqlUtilsCore.converter;

import com.fangxiong.globalUtils.GlobalCustomizeClazzDetector;
import com.fangxiong.globalUtils.GlobalConverterCacheLib;
import com.fangxiong.mysqlUtilsCore.enums.EnableCamelCaseToUnderscore;
import com.fangxiong.mysqlUtilsCore.exceptions.MysqlConverterError;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlObjectConverter implements MysqlNonGenericConverter {
    public static final Pattern camelToUnderscorePattern = Pattern.compile("(\\S+)([A-Z]\\S*)");

    @Override
    public Object converter(ResultSet resultSet, Class<?> clazz,String columName) {
        Object convertedObj = null;
        if(GlobalCustomizeClazzDetector.isCustomizeClazz(clazz)){
            int tempCursor = 0;
            Field[] converterFieldCache = GlobalConverterCacheLib.getConverterFieldCache(clazz);
            Map<Field, Method> converterSetMethodCache = GlobalConverterCacheLib.getConverterSetMethodCache(clazz);
            Map<Field,String> converterFiledNameCache = convertAllFiledNameToUnderscore(converterFieldCache);
            try{
                convertedObj = clazz.getDeclaredConstructor().newInstance();
            }catch(Exception e){
                throw new MysqlConverterError("Can't obtain the NoArgsConstructor from class : '"+clazz.getName()+"'.",e);
            }
            try {
                if (resultSet.next()){
                    for(Field f : converterFieldCache){
                        Object o = convertFiled(resultSet, clazz, f.getType(), f, converterFiledNameCache.get(f));
                        try {
                            converterSetMethodCache.get(f).invoke(convertedObj,o);
                        } catch (Exception e) {
                            throw new MysqlConverterError("Can't convert the value:'"+o+"',with method:"+converterSetMethodCache.get(f).getName()+".",e);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new MysqlConverterError("Access database error!",e);
            }
        }
        return convertedObj;
    }

    private static Object convertFiled(ResultSet resultSet,Class<?> clazz, Type type,Field field ,String columName){
        try {
            if(type instanceof ParameterizedType pt){
                return MysqlGenericConverterFactory.getConverter((Class<?>) pt.getRawType()).converter(resultSet,pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1]);
            }else{
                return MysqlNonGenericConverterFactory.getConverter((Class<?>) type).converter(resultSet,clazz,columName);
            }
        } catch (SQLException e) {
            throw new MysqlConverterError("Convert "+field.getName()+"failed,can't find the colum name:'"+columName+"',please check your field name's format or enable camelToUnderscore");
        }
    }

    private static Map<Field,String> convertAllFiledNameToUnderscore(Field[] fields){
        String tempName;
        Map<Field,String> nameList = new HashMap<>();
        if(MysqlNonGenericConverterFactory.getCamelCaseToUnderscoreStatus() == EnableCamelCaseToUnderscore.ENABLE){
            for(Field f : fields){
                Matcher matcher = camelToUnderscorePattern.matcher(f.getName());
                if(matcher.matches()){
                    tempName = matcher.group(1) + "_" +Character.toLowerCase(matcher.group(2).charAt(0))+matcher.group(2).substring(1);
                }else {
                    tempName = f.getName();
                }
                nameList.put(f,tempName);
            }
        }else{
            for(Field f : fields){
                nameList.put(f,f.getName());
            }
        }
        return nameList;
    }

//    private static ArrayList<String> cacheAllFieldName(Class<?> clazz, EnableUnderscoreToCamelCase enableUnderscoreToCamelCase){
//        Field[] df = clazz.getDeclaredFields();
//        ArrayList<String> tempFiledName = new ArrayList<>();
//        if (enableUnderscoreToCamelCase == EnableUnderscoreToCamelCase.ENABLE){
//            String tempStr;
//            for(Field f : df){
//                tempStr = f.getName();
//                if(tempStr.contains("_")){
//                    String[] split = tempStr.split("(\\S*)_(\\S*)");
//                    if(split.length!=2){
//                        throw new GlobalConverterCacheLibError("Convert UnderscoreToCamelCase Failed");
//                    }
//                    if(split[1].length()==1){
//                        tempStr = split[0]+Character.toUpperCase(split[1].charAt(0));
//                    }else{
//                        tempStr = split[0]+Character.toUpperCase(split[1].charAt(0))+split[1].substring(1);
//                    }
//                    tempFiledName.add(tempStr);
//                }
//            }
//            converterFiledNameCache.put(clazz,tempFiledName);
//            return tempFiledName;
//        }
//        for(Field f:df){
//            tempFiledName.add(f.getName());
//        }
//        converterFiledNameCache.put(clazz,tempFiledName);
//        return tempFiledName;
//    }
}
