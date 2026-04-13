package com.fangxiong.common;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class JSONUtils {
    public static String toJSONString(Object o) {
        Class<?> c = o.getClass();
        Field[] df = c.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        int tempCounter = 0;
        try {
            sb.append("{").append("\n");
            for (Field f : df) {
                f.setAccessible(true);
                sb.append("\"").append(f.getName()).append("\":").append("\"").append(f.get(o).toString()).append("\"");
                tempCounter++;
                if (df.length > 1 && tempCounter!=df.length) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    public static <T> T jsonToBean(String jsonString, Class<T> clazz) {
        T t = null;
        return t;
    }
}
