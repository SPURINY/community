package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* 自定义注解，利用元注解
* */
@Target(ElementType.METHOD)/*这个注解用于描述方法，用在方法之上*/
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
