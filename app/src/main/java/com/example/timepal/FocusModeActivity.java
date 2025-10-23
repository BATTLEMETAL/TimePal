package com.example.timepal;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FocusModeActivity extends AppCompatActivity {

    private TextView titleTextView, countdownTextView, progressTextView;
    private ProgressBar progressBar;
    private RecyclerView stepsRecyclerView;

    private TaskDatabase db;
    private Task currentTask;
    private List<TaskStep> steps;
    private TaskStepAdapter stepAdapter;

    private String mode;
    private MediaPlayer beepSound;
    private int intensityLevel = 0;
    private int tickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_mode);

        initViews();
        initDatabase();
        loadTaskAndSteps();
    }

    private void initViews() {
        titleTextView = findViewById(R.id.taskTitleFocus);
        countdownTextView = findViewById(R.id.countdownTimer);
        progressTextView = findViewById(R.id.progressTextView);
        progressBar = findViewById(R.id.progressBar);
        stepsRecyclerView = findViewById(R.id.stepsRecyclerView);

        SharedPreferences prefs = getSharedPreferences("TimePalPrefs", MODE_PRIVATE);
        mode = prefs.getString("pressure_mode", "normal");

        if ("hardcore".equals(mode)) {
            beepSound = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        }
    }

    private void initDatabase() {
        db = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, "task-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    private void loadTaskAndSteps() {
        int taskId = getIntent().getIntExtra("taskId", -1);
        currentTask = db.taskDao().getTaskById(taskId);

        if (currentTask != null) {
            titleTextView.setText(currentTask.title);
            startCountdown(currentTask.deadlineTimestamp);

            steps = db.taskStepDao().getStepsForTask(taskId);
            stepsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            stepAdapter = new TaskStepAdapter(steps, this, db, this::updateProgress);
            stepsRecyclerView.setAdapter(stepAdapter);

            updateProgress();
        } else {
            titleTextView.setText("Nie znaleziono zadania");
            countdownTextView.setText("--:--:--");
        }
    }

    private void startCountdown(long deadlineTimestamp) {
        long millisUntilFinished = deadlineTimestamp - System.currentTimeMillis();

        if (millisUntilFinished <= 0) {
            countdownTextView.setText("Po terminie!");
            return;
        }

        long tickInterval = "hardcore".equals(mode) ? 500 : 1000;
        boolean showPressure = "pressure".equals(mode) || "hardcore".equals(mode);

        new CountDownTimer(millisUntilFinished, tickInterval) {
            @Override
            public void onTick(long millisLeft) {
                long h = TimeUnit.MILLISECONDS.toHours(millisLeft);
                long m = TimeUnit.MILLISECONDS.toMinutes(millisLeft) % 60;
                long s = TimeUnit.MILLISECONDS.toSeconds(millisLeft) % 60;
                countdownTextView.setText(String.format("%02d:%02d:%02d", h, m, s));

                if (++tickCounter % 30 == 0 && showPressure) {
                    intensityLevel++;
                    showPressureMessage();
                }
            }

            @Override
            public void onFinish() {
                countdownTextView.setText("Czas minął!");
                Toast.makeText(FocusModeActivity.this, "Deadline osiągnięty!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void showPressureMessage() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);

        String[] mild = {"⏳ Pamiętaj o celu.", "🔔 Czas płynie spokojnie.", "👀 Skup się na jednym kroku."};
        String[] normal = {"🔥 Działaj już teraz!", "💡 Zrób cokolwiek TERAZ.", "⌛ Zegar tyka."};
        String[] intense = {"💀 Mało czasu!", "💣 DZIAŁAJ TERAZ!", "🚨 Maksymalne skupienie!"};

        String[] messageSet = (hour < 9 || hour >= 21 || dayOfWeek == Calendar.SUNDAY) ? mild :
                (intensityLevel < 2 ? mild : intensityLevel < 4 ? normal : intense);

        Toast.makeText(this, messageSet[new Random().nextInt(messageSet.length)], Toast.LENGTH_SHORT).show();

        if ("hardcore".equals(mode) && beepSound != null) {
            beepSound.start();
        }
    }

    private void updateProgress() {
        if (steps == null || steps.isEmpty()) {
            progressTextView.setText("Brak kroków");
            progressBar.setProgress(0);
            return;
        }

        int completed = 0;
        for (TaskStep step : steps) if (step.completed) completed++;

        int percent = (int) ((completed / (float) steps.size()) * 100);
        progressTextView.setText("Postęp: " + percent + "%");
        progressBar.setProgress(percent);
    }

    @Override
    public void onBackPressed() {
        if ("hardcore".equals(mode)) {
            Toast.makeText(this, "Nie możesz się wycofać w trybie Hardcore 💀", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ("hardcore".equals(mode) && steps != null) {
            for (TaskStep step : steps) {
                step.completed = false;
                db.taskStepDao().updateStep(step);
            }
            Toast.makeText(this, "Kroki zresetowane. Zaczynasz od nowa.", Toast.LENGTH_SHORT).show();
        }
        if (beepSound != null) beepSound.release();
    }
}
