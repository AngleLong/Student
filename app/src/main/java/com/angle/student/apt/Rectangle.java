package com.angle.student.apt;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;

import com.angle.annotation.Factory;

@Factory(id = "Rectangle", type = Shape.class)
public class Rectangle extends Shape {

    @Override
    public void draw(Canvas canvas, Paint paint) {
        System.out.println("Draw a Rectangle");
    }
}

