package com.angle.view;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

/**
 * 演示自定义ConstraintLayout 中的控件
 */
public class ConstraintLayoutActivity extends AppCompatActivity {

    private ConstraintLayout rootCL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_layout);

        rootCL = findViewById(R.id.rootCL);

        //动态布局添加一个顶部的按钮
        //1. 创建一个 ConstraintSet 对象用于构建
        ConstraintSet constraintSet = new ConstraintSet();
        //2. 构建一个Button对象
        Button button1 = new Button(this);
        button1.setId(View.generateViewId());
        button1.setText("动态添加的第一个按钮");
        //3. 添加一个对象
        rootCL.addView(button1);
        //4. 克隆一个ConstraintLayout
        constraintSet.clone(rootCL);
        //5. 添加相应的约束
        //宽度全屏
        constraintSet.constrainWidth(button1.getId(), ConstraintLayout.LayoutParams.MATCH_PARENT);
        //高度包裹内容
        constraintSet.constrainHeight(button1.getId(), ConstraintLayout.LayoutParams.WRAP_CONTENT);
        //相应的约束
        constraintSet.connect(button1.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(button1.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        constraintSet.connect(button1.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        constraintSet.applyTo(rootCL);

//        TransitionManager.
    }
}
