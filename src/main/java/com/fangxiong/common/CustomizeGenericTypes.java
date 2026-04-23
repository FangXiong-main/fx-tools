package com.fangxiong.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CustomizeGenericTypes implements ParameterizedType {
    private final Type rawType;
    private final Type[] actualTypes;

    public CustomizeGenericTypes(Type rawType,Type... actualTypes){
        this.rawType = rawType;
        this.actualTypes = actualTypes;
    }

    //Map<String,List<Object>>  //Map<String,Map<String,Map<String,String>>> //List<Map<String,String>>  //List<String>
    public CustomizeGenericTypes(String typeParams){
        Type tempRawType =null;
        ArrayList<Type> identifiedAcTypes = new ArrayList<>();
        StringBuilder sbRaw = new StringBuilder();
        StringBuilder sbActual = new StringBuilder();
        char[] ca = typeParams.toCharArray();
        int leftSignCount=0;
        boolean isReadingActualType = false;boolean hasMultipleGeneric = false;boolean isNestingGenericType =false;
        for(int i=0;i<ca.length;i++){
            if(ca[i]=='<' && !hasMultipleGeneric && !isNestingGenericType){
                leftSignCount++;
                tempRawType=CustomizeClazzDetector.getClazzWithStr(sbRaw.toString());
                sbRaw.setLength(0);
                isReadingActualType=true;
            } else if (ca[i]=='<' && leftSignCount==1 ) {
                isNestingGenericType = true;
                isReadingActualType = true;
                leftSignCount++;
            } else if (ca[i]=='>' && isNestingGenericType) {
                identifiedAcTypes.add(new CustomizeGenericTypes(sbRaw +"<"+ sbActual +">"));
                sbRaw.setLength(0);sbActual.setLength(0);
                break;
            } else if (ca[i]=='>' && hasMultipleGeneric) {
                identifiedAcTypes.add(CustomizeClazzDetector.getClazzWithStr(sbRaw.toString()));
                sbActual.setLength(0);
            } else if (ca[i]=='>' && !hasMultipleGeneric && !isNestingGenericType) {
                identifiedAcTypes.add(CustomizeClazzDetector.getClazzWithStr(sbActual.toString()));
                sbActual.setLength(0);
            } else if (!isNestingGenericType && isReadingActualType && ca[i]==',') {
                identifiedAcTypes.add(CustomizeClazzDetector.getClazzWithStr(sbActual.toString()));
                sbActual.setLength(0);
                isReadingActualType = false;
                hasMultipleGeneric = true;
            } else if (isReadingActualType) {
                sbActual.append(ca[i]);
            } else{
                sbRaw.append(ca[i]);
            }
        }
        this.rawType = tempRawType;
        this.actualTypes = identifiedAcTypes.toArray(new Type[]{});
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypes;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
