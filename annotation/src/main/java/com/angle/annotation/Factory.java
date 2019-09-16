package com.angle.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一个注解类,主要是APT的注解
 * 无法生成相应得文件得时候看最后一条评论
 * https://segmentfault.com/q/1010000018927725
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