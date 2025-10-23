package com.example.timepal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup modeGroup;
    private Switch themeSwitch;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "TimePalPrefs";
    private static final String KEY_MODE = "pressure_mode";
    private static final String KEY_THEME = "app_theme"; // "day" or "night"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applySavedTheme(); // Ustawienie motywu przed wyświetleniem layoutu
        setContentView(R.layout.activity_settings);

        modeGroup = findViewById(R.id.modeRadioGroup);
        themeSwitch = findViewById(R.id.themeSwitch);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        setupPressureMode();
        setupThemeSwitch();
    }

    private void setupPressureMode() {
        String currentMode = prefs.getString(KEY_MODE, "normal");
        int checkedId = R.id.radioNormal;
        if ("pressure".equals(currentMode)) checkedId = R.id.radioPressure;
        else if ("hardcore".equals(currentMode)) checkedId = R.id.radioHardcore;
        modeGroup.check(checkedId);

        modeGroup.setOnCheckedChangeListener((group, newCheckedId) -> {
            String selectedMode = "normal";
            if (newCheckedId == R.id.radioPressure) selectedMode = "pressure";
            else if (newCheckedId == R.id.radioHardcore) selectedMode = "hardcore";

            prefs.edit().putString(KEY_MODE, selectedMode).apply();
            Toast.makeText(this, "Wybrano tryb: " + formatMode(selectedMode), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupThemeSwitch() {
        String themePref = prefs.getString(KEY_THEME, "day");
        themeSwitch.setChecked("night".equals(themePref));

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String selectedTheme = isChecked ? "night" : "day";
            prefs.edit().putString(KEY_THEME, selectedTheme).apply();
            applyTheme(selectedTheme);
        });
    }

    private void applySavedTheme() {
        String themePref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_THEME, "day");
        applyTheme(themePref);
    }

    private void applyTheme(String themePref) {
        if ("night".equals(themePref)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private String formatMode(String mode) {
        switch (mode) {
            case "pressure": return "Presja";
            case "hardcore": return "Hardcore";
            default: return "Normalny";
        }
    }
}
