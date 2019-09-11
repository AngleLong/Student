package com.angle.student.apt;

import com.angle.annotation.Factory;

/**
 * 定义注解的类
 */
@Factory(id = "Rectangle", type = IShape.class)
public class Rectangle implements IShape {
    @Override
    public void draw() {
        System.out.println("Draw a Rectangle");
    }
}
