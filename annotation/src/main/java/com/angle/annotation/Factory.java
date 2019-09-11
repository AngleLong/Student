package com.angle.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一个注解类,主要是APT的注解
 *
 * @author hejinlong
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Factory {

    /**
     * 类的类型
     *
     * @return 相应的类
     */
    Class type();

    /**
     * id
     *
     * @return String类型参数
     */
    String id();
}