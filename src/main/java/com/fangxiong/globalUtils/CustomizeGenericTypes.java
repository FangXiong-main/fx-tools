package com.fangxiong.globalUtils;

import com.fangxiong.jsonUtilsCore.exceptions.CustomizeGenericError;
import com.fangxiong.jsonUtilsCore.coreUtil.JsonOperationUtil;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CustomizeGenericTypes implements ParameterizedType {

    private final Type rawType;
    private final Type[] actualTypes;

    public CustomizeGenericTypes(Type rawType,Type... actualTypes){
        if(rawType==null||actualTypes.length==0){
            throw new CustomizeGenericError("Creat CustomizeGenericType failed,Caused by rawType or actualTypes is null");
        }
        this.rawType = rawType;
        this.actualTypes = actualTypes;
    }

    public CustomizeGenericTypes(String typeParams){
        typeParams = JsonOperationUtil.getUndecoratedJSONStr(typeParams);
        Type tempRawType =null;
        ArrayList<Type> identifiedAcTypes = new ArrayList<>();
        StringBuilder sbRaw = new StringBuilder();
        StringBuilder sbActual = new StringBuilder();
        char[] ca = typeParams.toCharArray();
        int leftSignCount=0;int nestedLeftSignCount=0;
        boolean isReadingActualType = false;boolean hasMultipleGeneric = false;boolean isNestingGenericType =false;
        for(int i=0;i<ca.length;i++){
            if (ca[i]=='<' && leftSignCount==1 ) {
                nestedLeftSignCount++;
                isNestingGenericType = true;
                sbRaw.setLength(0);
                sbRaw.append(sbActual);
                sbActual.setLength(0);
                leftSignCount++;
            } else if(ca[i]=='<' && !hasMultipleGeneric && !isNestingGenericType){
                leftSignCount++;
                tempRawType= CustomizeClazzDetector.getClazzWithStr(sbRaw.toString());
                sbRaw.setLength(0);
                isReadingActualType= true;
            } else if (ca[i]=='<' && isNestingGenericType) {
                nestedLeftSignCount++;sbActual.append(ca[i]);
            } else if (ca[i]=='>'&&isNestingGenericType && nestedLeftSignCount==1) {
                identifiedAcTypes.add(new CustomizeGenericTypes(sbRaw +"<"+ sbActual +">"));
                sbRaw.setLength(0);sbActual.setLength(0);
                break;
            } else if (ca[i]=='>' && isNestingGenericType) {
                nestedLeftSignCount--;sbActual.append(ca[i]);
            } else if (ca[i]=='>' && hasMultipleGeneric) {
                identifiedAcTypes.add(CustomizeClazzDetector.getClazzWithStr(sbActual.toString()));
                sbActual.setLength(0);
            } else if (ca[i]=='>' && !hasMultipleGeneric && !isNestingGenericType) {
                identifiedAcTypes.add(CustomizeClazzDetector.getClazzWithStr(sbActual.toString()));
                sbActual.setLength(0);
            }else if (!isNestingGenericType && isReadingActualType && ca[i]==',') {
                identifiedAcTypes.add(CustomizeClazzDetector.getClazzWithStr(sbActual.toString()));
                sbActual.setLength(0);
                hasMultipleGeneric = true;
            } else if (isReadingActualType) {
                sbActual.append(ca[i]);
            } else{
                sbRaw.append(ca[i]);
            }
        }
        if (identifiedAcTypes.size()==2){
            if(tempRawType != null && identifiedAcTypes.get(0) != null && identifiedAcTypes.get(1) != null){
                this.rawType = tempRawType;
                this.actualTypes = identifiedAcTypes.toArray(new Type[]{});
            }else {
                throw new CustomizeGenericError("Converting '"+typeParams+"'"+" failed,Caused by syntax error");
            }
        }else{
            if(tempRawType != null && !identifiedAcTypes.isEmpty() && identifiedAcTypes.get(0) != null){
                this.rawType = tempRawType;
                this.actualTypes = identifiedAcTypes.toArray(new Type[]{});
            }else {
                throw new CustomizeGenericError("Converting '"+typeParams+"'"+" failed,Caused by syntax error");
            }
        }
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
