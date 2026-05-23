package com.fangxiong.jsonUtilsCore.coreUtil;

import com.fangxiong.globalUtils.GlobalConverterCacheLib;
import com.fangxiong.globalUtils.GlobalCustomizeClazzDetector;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

public class EntityStructureCacheManager {
    private static final Map<Class<?>, List<EntityStructureCache>> entityStructurePattern = new HashMap<>();

    public static List<EntityStructureCache> getEntityStructurePattern(Class<?> clazz){
        List<EntityStructureCache> entityStructureCaches = entityStructurePattern.get(clazz);
        if (entityStructureCaches != null){
            return entityStructureCaches;
        }
        return cacheAllAttributeStructure(clazz);
    }

    private static List<EntityStructureCache> cacheAllAttributeStructure(Class<?> clazz){
        StringBuilder sb = new StringBuilder();
        Field[] fd = GlobalConverterCacheLib.getConverterFieldCache(clazz);
        List<EntityStructureCache> attributePattern = new ArrayList<>();
        for(int i=0;i<fd.length;i++){
            String name = fd[i].getName();
            sb.append("\"").append(name).append("\"").append(":").append(structurePatternGenerator(fd[i].getType()));
            if(i < fd.length-1){
                sb.append(",").append("|\"").append(name).append("\"").append(":").append("(null)");
            } else if (i == fd.length-1) {
                sb.append("}").append("|\"").append(name).append("\"").append(":").append("(null)");
            }
            attributePattern.add(new EntityStructureCache(name,Pattern.compile(sb.toString())));
            sb.setLength(0);
        }
        entityStructurePattern.put(clazz,attributePattern);
        return attributePattern;
    }

    private static String structurePatternGenerator(Class<?> clazz){
        if(clazz == Map.class){
            return "(\\S+)";
        } else if (clazz == List.class || clazz == Set.class) {
            return "(\\S+)";
        } else {
            if (GlobalCustomizeClazzDetector.isDigitType(clazz)){
                if (GlobalCustomizeClazzDetector.isIntegerType(clazz)){
                    return "-?(\\d+)";
                }else {
                    return "-?(\\d+\\.\\d+)";
                }
            } else if (clazz == Boolean.class || clazz == boolean.class) {
                return "(true|false|0|1)";
            } else {
                return "\"(\\S+?|)\"";
            }
        }
    }
}
