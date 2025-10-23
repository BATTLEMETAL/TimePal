package com.example.timepal;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import com.example.timepal.network.AiService;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddTaskActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private TextView deadlineDisplay;
    private Button saveButton, addStepButton, suggestStepsButton;
    private LinearLayout stepsContainer;
    private ProgressBar aiProgressBar;
    private long selectedDeadline = 0;
    private TaskDatabase db;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final int REQUEST_CODE_NOTIFICATIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        initDatabase();
        setupListeners();
        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATIONS
                );
            }
        }
    }

    private void initViews() {
        titleInput = findViewById(R.id.taskTitleInput);
        descriptionInput = findViewById(R.id.taskDescriptionInput);
        deadlineDisplay = findViewById(R.id.deadlineTextView);
        saveButton = findViewById(R.id.saveTaskButton);
        addStepButton = findViewById(R.id.addStepButton);
        suggestStepsButton = findViewById(R.id.suggestStepsButton);
        stepsContainer = findViewById(R.id.stepsContainer);
        aiProgressBar = findViewById(R.id.aiLoadingSpinner);
    }

    private void initDatabase() {
        db = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, "task-db")
                .fallbackToDestructiveMigration()
                .build();
    }

    private void setupListeners() {
        deadlineDisplay.setOnClickListener(view -> showDateTimePicker());

        addStepButton.setOnClickListener(v -> addStepField());

        suggestStepsButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            if (!title.isEmpty()) {
                aiProgressBar.setVisibility(View.VISIBLE);
                fetchStepsFromAI(title);
            } else {
                Toast.makeText(this, "Podaj tytuł zadania", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(view -> saveTask());
    }

    private void addStepField() {
        EditText stepInput = new EditText(this);
        stepInput.setHint("Opis kroku");
        stepInput.setInputType(InputType.TYPE_CLASS_TEXT);
        stepInput.setImeOptions(EditorInfo.IME_ACTION_DONE);

        stepInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                addStepField();
                return true;
            }
            return false;
        });

        stepsContainer.addView(stepInput);
        stepInput.requestFocus();
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dateDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    TimePickerDialog timeDialog = new TimePickerDialog(this,
                            (view1, hour, minute) -> {
                                calendar.set(year, month, day, hour, minute);
                                selectedDeadline = calendar.getTimeInMillis();
                                deadlineDisplay.setText(String.format(Locale.getDefault(), "%02d.%02d.%d %02d:%02d",
                                        day, month + 1, year, hour, minute));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timeDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    private void fetchStepsFromAI(String title) {
        AiService.getInstance(this).fetchSteps(title, new AiService.SuggestionCallback() {
            @Override
            public void onSuccess(String steps) {
                runOnUiThread(() -> {
                    aiProgressBar.setVisibility(View.GONE);
                    for (String lineStep : steps.split("\n")) {
                        String step = lineStep.replaceAll("^[0-9.\\-\\s]*", "").trim();
                        if (!step.isEmpty()) {
                            EditText stepInput = new EditText(AddTaskActivity.this);
                            stepInput.setText(step);
                            stepInput.setInputType(InputType.TYPE_CLASS_TEXT);
                            stepsContainer.addView(stepInput);
                        }
                    }
                    Toast.makeText(AddTaskActivity.this, "Kroki wygenerowane przez AI", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    aiProgressBar.setVisibility(View.GONE);
                    Toast.makeText(AddTaskActivity.this, "Błąd AI: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFallback(String cachedSteps) {
                runOnUiThread(() -> {
                    aiProgressBar.setVisibility(View.GONE);
                    if (cachedSteps.isEmpty()) {
                        Toast.makeText(AddTaskActivity.this, "Brak danych offline", Toast.LENGTH_LONG).show();
                    } else {
                        for (String lineStep : cachedSteps.split("\n")) {
                            String step = lineStep.replaceAll("^[0-9.\\-\\s]*", "").trim();
                            if (!step.isEmpty()) {
                                EditText stepInput = new EditText(AddTaskActivity.this);
                                stepInput.setText(step);
                                stepInput.setInputType(InputType.TYPE_CLASS_TEXT);
                                stepsContainer.addView(stepInput);
                            }
                        }
                        Toast.makeText(AddTaskActivity.this, "Wyświetlono ostatnie sugestie offline", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void saveTask() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (title.isEmpty() || selectedDeadline == 0) {
            Toast.makeText(this, "Wpisz tytuł i wybierz deadline", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task();
        task.title = title;
        task.description = description;
        task.deadlineTimestamp = selectedDeadline;

        List<TaskStep> stepList = new ArrayList<>();
        Set<String> addedSteps = new HashSet<>();

        for (int i = 0; i < stepsContainer.getChildCount(); i++) {
            View child = stepsContainer.getChildAt(i);
            if (child instanceof EditText) {
                String stepText = ((EditText) child).getText().toString().trim();
                if (!stepText.isEmpty() && addedSteps.add(stepText)) {
                    TaskStep step = new TaskStep();
                    step.stepText = stepText;
                    step.completed = false;
                    stepList.add(step);
                }
            }
        }

        executor.execute(() -> {
            long taskId = db.taskDao().insertTaskReturnId(task);
            task.id = (int) taskId;
            for (TaskStep step : stepList) step.taskId = task.id;
            db.taskStepDao().insertSteps(stepList);

            runOnUiThread(() -> {
                scheduleDeadlineNotification(task);
                Toast.makeText(this, "Zadanie zapisane", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void scheduleDeadlineNotification(Task task) {
        Intent intent = new Intent(this, DeadlineReceiver.class);
        intent.putExtra("task_title", task.title);

        int uniqueId = (task.title + task.deadlineTimestamp).hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, uniqueId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long notifyTime = task.deadlineTimestamp - 3600_000; // 1h przed deadline

        if (notifyTime > System.currentTimeMillis()) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Zgoda na powiadomienia przyznana", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Brak zgody na powiadomienia – mogą się nie wyświetlać", Toast.LENGTH_LONG).show();
            }
        }
    }
}
