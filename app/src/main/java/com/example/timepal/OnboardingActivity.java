package com.example.timepal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Ekran powitalny, wyświetlany tylko przy pierwszym uruchomieniu aplikacji.
 */
public class OnboardingActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TimePalPrefs";
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true);

        // Jeśli nie jest to pierwsze uruchomienie, przejdź od razu do MainActivity
        if (!isFirstLaunch) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
            startMainActivity();
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
