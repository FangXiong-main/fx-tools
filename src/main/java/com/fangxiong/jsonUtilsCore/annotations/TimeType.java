package com.fangxiong.jsonUtilsCore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TimeType {
    String value() default "yyyy-MM-dd'T'HH:mm:ss";
}
