package com.enx.pipedrop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    private Button orificeCalcButton;
    private Button ppmCalcButton;
    private Button manualButton;
    private TextView versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        orificeCalcButton = (Button) findViewById(R.id.orificeButton);
        orificeCalcButton.setOnClickListener(this::onOrificeClick);
        ppmCalcButton = (Button) findViewById(R.id.ppmBtn);
        ppmCalcButton.setOnClickListener(this::onPpmClick);
        manualButton = (Button) findViewById(R.id.manualButton);
        manualButton.setOnClickListener(this::onManualClick);
        versionName = (TextView)  findViewById(R.id.verName);
        versionName.setText("Version: " + BuildConfig.VERSION_NAME);
    }

    private void onOrificeClick(View view) {
        Intent orificeClick = new Intent(MainMenu.this, MainActivity.class);
        startActivity(orificeClick);
    }

    private void onManualClick(View view) {
        Intent manualClick = new Intent(MainMenu.this, Manual.class);
        startActivity(manualClick);
    }

    private void onPpmClick(View view) {
        Intent ppmClick = new Intent(MainMenu.this, ppmCalculator.class);
        startActivity(ppmClick);
    }
}