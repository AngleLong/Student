package com.angle.view;

import android.content.Intent;
import android.os.Build;
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
    private Button mButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_layout);

        rootCL = findViewById(R.id.rootCL);

        //添加一个按钮
        addView();
        initListener();
    }

    public void addView() {
        //动态布局添加一个顶部的按钮
        //1. 创建一个 ConstraintSet 对象用于构建
        ConstraintSet constraintSet = new ConstraintSet();
        //2. 构建一个Button对象
        mButton1 = new Button(this);
        //这个是一个17的方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mButton1.setId(View.generateViewId());
        } else {
            mButton1.setId(R.id.addButton);
        }
        mButton1.setText("跳转到控件的动画页面");
        //3. 添加一个对象
        rootCL.addView(mButton1);
        //4. 克隆一个ConstraintLayout
        constraintSet.clone(rootCL);
        //下面的注释和上面是一样的
        //constraintSet.clone(this,R.layout.activity_constraint_layout);
        //5. 添加相应的约束
        //宽度全屏
        constraintSet.constrainWidth(mButton1.getId(), ConstraintLayout.LayoutParams.MATCH_PARENT);
        //高度包裹内容
        constraintSet.constrainHeight(mButton1.getId(), ConstraintLayout.LayoutParams.WRAP_CONTENT);
        //相应的约束
        constraintSet.connect(mButton1.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(mButton1.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        constraintSet.connect(mButton1.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        constraintSet.applyTo(rootCL);
    }

    private void initListener() {
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ConstraintLayoutActivity.this,AnimationActivity.class));
            }
        });
    }
}
