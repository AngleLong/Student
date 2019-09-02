package com.angle.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;

import com.angle.mediarecorder.MainLibActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void alarmClick(View view) {
        Intent intent = new Intent(this, MainLibActivity.class);
        startActivity(intent);
    }
}
