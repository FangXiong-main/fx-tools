package com.fangxiong.jsonUtilsCore.coreUtil;

import java.util.regex.Pattern;

public class EntityStructureCache {
    private String fieldName;
    private Pattern fieldPattern;

    public EntityStructureCache(String fieldName, Pattern fieldPattern) {
        this.fieldName = fieldName;
        this.fieldPattern = fieldPattern;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Pattern getFieldPattern() {
        return fieldPattern;
    }
}
