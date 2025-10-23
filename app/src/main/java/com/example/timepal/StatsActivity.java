package com.example.timepal;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatsActivity extends AppCompatActivity {

    private TextView tasksCompletedText, avgProgressText;
    private TextView tasksThisWeekText, avgDeadlineText, avgStepsText;
    private BarChart barChart, stepsPerTaskChart;
    private TaskDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        tasksCompletedText = findViewById(R.id.tasksCompletedText);
        avgProgressText = findViewById(R.id.avgProgressText);
        tasksThisWeekText = findViewById(R.id.tasksThisWeekText);
        avgDeadlineText = findViewById(R.id.avgDeadlineText);
        avgStepsText = findViewById(R.id.avgStepsText);
        barChart = findViewById(R.id.progressBarChart);
        stepsPerTaskChart = findViewById(R.id.stepsPerTaskChart);

        initDatabase();
        loadStatistics();
    }

    private void initDatabase() {
        db = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, "task-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    private void loadStatistics() {
        List<Task> tasks = db.taskDao().getAllTasks();
        if (tasks == null || tasks.isEmpty()) {
            tasksCompletedText.setText("Brak zadań.");
            avgProgressText.setText("Średni postęp: 0%");
            return;
        }

        int totalProgress = 0;
        int fullyCompleted = 0;
        Map<Integer, Integer> dayProgress = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        long startOfWeek = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_WEEK, 7);
        long endOfWeek = cal.getTimeInMillis();

        int tasksThisWeek = 0;
        long now = System.currentTimeMillis();
        long sumTimeToDeadline = 0;
        int futureTasks = 0;
        int totalSteps = 0;

        List<BarEntry> stepEntries = new ArrayList<>();
        List<String> taskLabels = new ArrayList<>();
        int taskIndex = 0;

        for (Task task : tasks) {
            List<TaskStep> steps = db.taskStepDao().getStepsForTask(task.id);
            if (steps.isEmpty()) continue;

            int completedSteps = 0;
            for (TaskStep step : steps) {
                if (step.completed) completedSteps++;
            }

            int progress = (int) ((completedSteps / (float) steps.size()) * 100);
            totalProgress += progress;
            if (progress == 100) fullyCompleted++;

            Calendar taskCal = Calendar.getInstance();
            taskCal.setTimeInMillis(task.deadlineTimestamp);
            int dayOfWeek = taskCal.get(Calendar.DAY_OF_WEEK);
            dayProgress.put(dayOfWeek, dayProgress.getOrDefault(dayOfWeek, 0) + progress);

            if (task.deadlineTimestamp >= startOfWeek && task.deadlineTimestamp < endOfWeek) {
                tasksThisWeek++;
            }

            if (task.deadlineTimestamp > now) {
                sumTimeToDeadline += (task.deadlineTimestamp - now);
                futureTasks++;
            }

            totalSteps += steps.size();
            stepEntries.add(new BarEntry(taskIndex, steps.size()));
            taskLabels.add("Zad. " + (taskIndex + 1));
            taskIndex++;
        }

        int avgProgress = totalProgress / tasks.size();
        long avgDeadlineHours = futureTasks > 0 ? TimeUnit.MILLISECONDS.toHours(sumTimeToDeadline / futureTasks) : 0;
        double avgSteps = tasks.isEmpty() ? 0 : (double) totalSteps / tasks.size();

        tasksCompletedText.setText("Ukończone zadania: " + fullyCompleted + " / " + tasks.size());
        avgProgressText.setText("Średni postęp: " + avgProgress + "%");
        tasksThisWeekText.setText("Zadania w tym tygodniu: " + tasksThisWeek);
        avgDeadlineText.setText("Średni czas do deadline: " + avgDeadlineHours + " h");
        avgStepsText.setText("Śr. liczba kroków: " + String.format(Locale.getDefault(), "%.1f", avgSteps));

        setupDayProgressChart(dayProgress);
        setupStepsPerTaskChart(stepEntries, taskLabels);
    }

    private void setupDayProgressChart(Map<Integer, Integer> dayProgress) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            entries.add(new BarEntry(i, dayProgress.getOrDefault(i, 0)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Postęp wg dni tygodnia");
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        String[] days = {"", "Nd", "Pn", "Wt", "Śr", "Cz", "Pt", "Sb"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(800);
        barChart.invalidate();
    }

    private void setupStepsPerTaskChart(List<BarEntry> entries, List<String> labels) {
        BarDataSet dataSet = new BarDataSet(entries, "Kroki na zadanie");
        BarData barData = new BarData(dataSet);
        stepsPerTaskChart.setData(barData);

        XAxis xAxis = stepsPerTaskChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        stepsPerTaskChart.getDescription().setEnabled(false);
        stepsPerTaskChart.getAxisRight().setEnabled(false);
        stepsPerTaskChart.animateY(800);
        stepsPerTaskChart.invalidate();
    }
}
