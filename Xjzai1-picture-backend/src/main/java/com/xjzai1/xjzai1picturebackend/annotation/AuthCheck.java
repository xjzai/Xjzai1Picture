package com.xjzai1.xjzai1picturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME )
public @interface AuthCheck {

    /**
     * 必须拥有某个权限
     * @return
     */
    String mustRole() default "";
}
