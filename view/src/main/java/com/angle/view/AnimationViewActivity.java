package com.angle.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;

/**
 * @author hejinlong
 * 展示View的动画
 */
public class AnimationViewActivity extends AppCompatActivity {

    private ConstraintSet animationSet = new ConstraintSet();
    private ConstraintSet reductionSet = new ConstraintSet();
    private ConstraintLayout mClMain;
    /**
     * 是否还原的判断
     */
    private boolean isReduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_view);

        mClMain = findViewById(R.id.main);

        animationSet.clone(mClMain);
        reductionSet.clone(mClMain);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onApplyClick(View view) {
        TransitionManager.beginDelayedTransition(mClMain);
        if (!isReduction) {

            //清理一下控件的约束
            animationSet.clear(R.id.iv_icon);
            animationSet.clear(R.id.tv_des);
            animationSet.clear(R.id.tv_all);
            animationSet.clear(R.id.tv_all);

            //设置顶部图片
            animationSet.constrainWidth(R.id.iv_icon, ConstraintLayout.LayoutParams.MATCH_PARENT);
            animationSet.constrainHeight(R.id.iv_icon, ConstraintLayout.LayoutParams.WRAP_CONTENT);

            //设置文字
            animationSet.constrainWidth(R.id.tv_des, ConstraintLayout.LayoutParams.MATCH_PARENT);
            animationSet.constrainHeight(R.id.tv_des, 0);

            //设置按钮的位置
            animationSet.constrainWidth(R.id.tv_all, ConstraintLayout.LayoutParams.MATCH_PARENT);
            animationSet.constrainHeight(R.id.tv_all, 0);

            //设置变化后的位置
            animationSet.connect(R.id.iv_icon, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            animationSet.connect(R.id.tv_des, ConstraintSet.TOP, R.id.iv_icon, ConstraintSet.BOTTOM);
            animationSet.connect(R.id.tv_des, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            animationSet.connect(R.id.tv_all, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

            animationSet.applyTo(mClMain);
            isReduction = true;
        } else {
            reductionSet.applyTo(mClMain);
            isReduction = false;
        }
    }
}
