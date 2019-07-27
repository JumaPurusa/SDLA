package com.example.jay.sdla;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.example.jay.sdla.Activities.EnterPatternActivity;
import com.example.jay.sdla.Activities.MainActivity;

public class LoadingActivity extends AppCompatActivity {

    ProgressBar progressBar;
    int progress = 0;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        new Thread(new Runnable() {
            @Override
            public void run() {

                for(int i=0; i<5; i++){
                    progress += 20;

                    handler.post(new Runnable() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                            if(progress == progressBar.getMax()){

                                SharedPreferences preferences = getSharedPreferences("PREFS", 0);
                                String password = preferences.getString("password", "0");

                                if(password.equals("0")){
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }else{
                                    startActivity(new Intent(getApplicationContext(), EnterPatternActivity.class));
                                    finish();
                                }
                            }
                        }
                    });

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
