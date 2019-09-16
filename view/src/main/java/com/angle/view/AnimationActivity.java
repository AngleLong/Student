package com.angle.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;

/**
 * @author hejinlong
 * 演示ConstraintLayout的简单控件移动
 */
public class AnimationActivity extends AppCompatActivity {

    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private ConstraintSet resetConstraintSet = new ConstraintSet();
    private ConstraintLayout mMainCL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        mMainCL = findViewById(R.id.main);

        //分别克隆出两个状态
        applyConstraintSet.clone(mMainCL);
        resetConstraintSet.clone(mMainCL);

    }

    /**
     * 开始动作
     *
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void applyClick(View view) {
        //这里定义一个比较复杂的就能说明一切了!
        TransitionManager.beginDelayedTransition(mMainCL);

        applyConstraintSet.clear(R.id.button1);
        applyConstraintSet.clear(R.id.button2);
        applyConstraintSet.clear(R.id.button3);

        //设置顶部位置
        applyConstraintSet.connect(R.id.button1, ConstraintSet.TOP, R.id.main, ConstraintSet.TOP);
        applyConstraintSet.connect(R.id.button2, ConstraintSet.TOP, R.id.main, ConstraintSet.TOP);
        applyConstraintSet.connect(R.id.button3, ConstraintSet.TOP, R.id.main, ConstraintSet.TOP);

        //设置左右位置
        applyConstraintSet.connect(R.id.button1, ConstraintSet.LEFT, R.id.main, ConstraintSet.LEFT);
        applyConstraintSet.connect(R.id.button1, ConstraintSet.RIGHT, R.id.button2, ConstraintSet.LEFT);

        applyConstraintSet.connect(R.id.button2, ConstraintSet.LEFT, R.id.button1, ConstraintSet.RIGHT);
        applyConstraintSet.connect(R.id.button2, ConstraintSet.RIGHT, R.id.button3, ConstraintSet.LEFT);

        applyConstraintSet.connect(R.id.button3, ConstraintSet.LEFT, R.id.button2, ConstraintSet.RIGHT);
        applyConstraintSet.connect(R.id.button3, ConstraintSet.RIGHT, R.id.main, ConstraintSet.RIGHT);

        //设置大小的
        applyConstraintSet.constrainWidth(R.id.button1, 0);
        applyConstraintSet.constrainWidth(R.id.button2, 0);
        applyConstraintSet.constrainWidth(R.id.button3, 0);

        applyConstraintSet.constrainHeight(R.id.button1, ConstraintSet.WRAP_CONTENT);
        applyConstraintSet.constrainHeight(R.id.button2, ConstraintSet.WRAP_CONTENT);
        applyConstraintSet.constrainHeight(R.id.button3, ConstraintSet.WRAP_CONTENT);

        /*
         * 参数1:左边的基准线
         * 参数2:左边基准线的约束
         * 参数3:右边的基准线
         * 参数4:右边基准线的约束
         */
        applyConstraintSet.createHorizontalChain(R.id.main, ConstraintSet.LEFT,
                R.id.main, ConstraintSet.RIGHT,
                new int[]{R.id.button1, R.id.button2, R.id.button3}, new float[]{1f, 2f, 1f}, ConstraintWidget.CHAIN_SPREAD_INSIDE);

        applyConstraintSet.applyTo(mMainCL);
    }

    /**
     * 重置动作
     *
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resetClick(View view) {
        TransitionManager.beginDelayedTransition(mMainCL);
        resetConstraintSet.applyTo(mMainCL);
    }
}
