package com.angle.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

/**
 * 展示图形锁
 */
public class PatternLockActivity extends AppCompatActivity {

    private static final String TAG = PatternLockActivity.class.getSimpleName();
    private PatternLockView mPatternLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock);


        mPatternLockView = findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
                Log.e(TAG, "onStarted: ");
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
                Log.d(getClass().getName(), "Pattern progress: " +
                        PatternLockUtils.patternToString(mPatternLockView, progressPattern));
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if (pattern.size() < 4) {
                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                } else {
                    Log.d(getClass().getName(), "Pattern complete: " +
                            PatternLockUtils.patternToSha1(mPatternLockView, pattern));
                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                }
            }

            @Override
            public void onCleared() {
                Log.d(getClass().getName(), "Pattern has been cleared");
            }
        });
    }
}
