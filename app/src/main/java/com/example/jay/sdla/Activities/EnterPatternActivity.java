package com.example.jay.sdla.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.example.jay.sdla.R;

import java.util.List;

public class EnterPatternActivity extends AppCompatActivity {

    PatternLockView mPatternLockView;

    //TextView wrongPattern;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pattern);

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        password = preferences.getString("password", "0");

        //wrongPattern = findViewById(R.id.wrong_pattern);
        //wrongPattern.setVisibility(View.GONE);

        mPatternLockView = findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(
                new PatternLockViewListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onProgress(List<PatternLockView.Dot> progressPattern) {

                    }

                    @Override
                    public void onComplete(List<PatternLockView.Dot> pattern) {

                        if(password.equals(PatternLockUtils.patternToString(mPatternLockView, pattern))){
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }else{
                            //wrongPattern.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Wrong Pattern, Please try again", Toast.LENGTH_SHORT).show();
                            mPatternLockView.clearPattern();
                        }


                    }

                    @Override
                    public void onCleared() {

                    }
                }
        );
    }
}
