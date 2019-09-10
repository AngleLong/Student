package com.angle.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.angle.view.AnimationViewActivity;
import com.angle.view.ConstraintLayoutActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void alarmClick(View view) {
        Intent intent = new Intent(this, AnimationViewActivity.class);
        startActivity(intent);
    }
}
