package com.example.jay.sdla.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.example.jay.sdla.Dialogs.EnterSecurityKeyDialog;
import com.example.jay.sdla.R;

public class SettingsActivity extends AppCompatActivity implements EnterSecurityKeyDialog.Communicator{

    LinearLayout settingPattern, settingSecuriy;
    SharedPreferences keySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0,0, 0, 0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        keySettings = this.getSharedPreferences("keySettings", MODE_PRIVATE);

        settingSecuriy = findViewById(R.id.setting_security_part);

        settingSecuriy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EnterSecurityKeyDialog dialog = new EnterSecurityKeyDialog();
                        dialog.show(getSupportFragmentManager(), "Dialog");
                    }
                }
        );

        String name = keySettings.getString("password", "");
        settingPattern = findViewById(R.id.setting_pattern_part);
        settingPattern.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), SetPatternActivity.class));
                    }
                }
        );
    }

    @Override
    public void passingKey(String key) {

        SharedPreferences.Editor editor = keySettings.edit();
        editor.clear();
        editor.putString("password", key);
        editor.commit();
    }
}
