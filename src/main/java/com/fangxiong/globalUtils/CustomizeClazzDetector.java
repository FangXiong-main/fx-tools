package com.fangxiong.globalUtils;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


public class CustomizeClazzDetector {
    private static final ArrayList<Class<?>> clazzList = new ArrayList<>();
    private static final ArrayList<Class<?>> genericClazzList = new ArrayList<>();
    private static final Map<String,Class<?>> detClazzWithStr = new HashMap<>();
    static {
        clazzList.add(Integer.class);
        clazzList.add(Long.class);
        clazzList.add(Short.class);
        clazzList.add(Byte.class);
        clazzList.add(Double.class);
        clazzList.add(Float.class);
        clazzList.add(Boolean.class);
        clazzList.add(Character.class);
        clazzList.add(String.class);
        clazzList.add(BigDecimal.class);
        clazzList.add(BigInteger.class);
        clazzList.add(LocalDate.class);
        clazzList.add(LocalTime.class);
        clazzList.add(LocalDateTime.class);
        clazzList.add(Date.class);
        clazzList.add(Timestamp.class);
        clazzList.add(List.class);
        clazzList.add(Map.class);
        clazzList.add(Set.class);
        clazzList.add(Object.class);
        clazzList.add(Class.class);
        clazzList.add(int.class);
        clazzList.add(long.class);
        clazzList.add(short.class);
        clazzList.add(byte.class);
        clazzList.add(double.class);
        clazzList.add(float.class);
        clazzList.add(boolean.class);
        clazzList.add(char.class);

        genericClazzList.add(Map.class);
        genericClazzList.add(List.class);

        detClazzWithStr.put("String",String.class);
        detClazzWithStr.put("Map",Map.class);
        detClazzWithStr.put("Object",Object.class);
        detClazzWithStr.put("Boolean",Boolean.class);
        detClazzWithStr.put("Integer",Integer.class);
        detClazzWithStr.put("Double",Double.class);
        detClazzWithStr.put("ArrayList",ArrayList.class);
        detClazzWithStr.put("List",List.class);
        detClazzWithStr.put("Set",Set.class);
    }

    public static Class<?> getClazzWithStr(String str){
        return detClazzWithStr.get(str);
    }

    public static Boolean isGenericTypeClazz(Class<?> clazz){
        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> i : interfaces){
            if(genericClazzList.contains(i)){
                return true;
            }
        }
        return false;
    }

    public static Boolean isImplOfInterface(Class<?> implEntityClazz,Class<?> interfaceClazz){
        Class<?>[] interfaces = implEntityClazz.getInterfaces();
        for(Type t : interfaces){
            if(t==interfaceClazz){
                return true;
            }
        }
        return false;
    }

    public static Boolean isCustomizeClazz(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> i : interfaces) {
            if (clazzList.contains(i)) {
                return false;
            }
        }
        return !clazzList.contains(clazz);
    }

}
