package com.fangxiong.mysqlUtilsCore.converter;

import com.fangxiong.globalUtils.CustomizeClazzDetector;
import com.fangxiong.globalUtils.GlobalConverterCacheLib;
import com.fangxiong.mysqlUtilsCore.EnableUnderscoreToCamelCase;
import com.fangxiong.mysqlUtilsCore.exceptions.MysqlConverterError;
import com.fangxiong.utils.mysql.MysqlUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlObjectNonGenericConverter implements MysqlNonGenericConverter {
    public static final Pattern camelToUnderscorePattern = Pattern.compile("(\\S+)([A-Z]\\S*)");

    @Override
    public Object converter(ResultSet resultSet, Class<?> clazz,String columName) {
        if(!CustomizeClazzDetector.isCustomizeClazz(clazz)){
            int tempCursor = 0;
            Field[] converterFieldCache = GlobalConverterCacheLib.getConverterFieldCache(clazz);
            ArrayList<String> converterFiledNameCache = convertAllFiledNameToUnderscore(converterFieldCache);
            try{
                Object convertedObj = clazz.getDeclaredConstructor().newInstance();
            }catch(Exception e){
                throw new MysqlConverterError("Can't obtain the NoArgsConstructor from class : '"+clazz.getName()+"'.",e);
            }
            try {
                while (resultSet.next()){
                    String typeStr = converterFieldCache[tempCursor].getType().getName();
                    //MysqlNonGenericConverterFactory.getValueWithType(resultSet)
                }
            } catch (SQLException e) {
                throw new MysqlConverterError("Access database error!",e);
            }
        }else {

        }
        return null;
    }

    private static ArrayList<String> convertAllFiledNameToUnderscore(Field[] fields){
        String tempName;
        ArrayList<String> nameList = new ArrayList<>();
        if(MysqlUtils.underscoreToCamelCaseEnum()==EnableUnderscoreToCamelCase.ENABLE){
            for(Field f : fields){
                Matcher matcher = camelToUnderscorePattern.matcher(f.getName());
                if(matcher.matches()){
                    tempName = matcher.group(1) + "_" +Character.toLowerCase(matcher.group(2).charAt(0))+matcher.group(2).substring(1);
                }else {
                    tempName = f.getName();
                }
                nameList.add(tempName);
            }
        }else{
            for(Field f : fields){
                nameList.add(f.getName());
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
