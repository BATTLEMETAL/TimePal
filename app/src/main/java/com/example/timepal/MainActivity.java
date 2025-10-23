package com.example.timepal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

/**
 * Ekran główny – wyświetla listę zadań oraz przyciski do statystyk i dodawania zadań.
 */
public class MainActivity extends AppCompatActivity {

    private TaskDatabase db;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    private Button addTaskButton, viewStatsButton, openSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();       // Inicjalizacja elementów UI
        initDatabase();    // Utworzenie instancji bazy
        setupListeners();  // Nasłuchiwanie na kliknięcia
        loadTasks();       // Załaduj dane do listy
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks(); // Odśwież dane po powrocie z innego ekranu
    }

    private void initViews() {
        recyclerView = findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addTaskButton = findViewById(R.id.addTaskButton);
        viewStatsButton = findViewById(R.id.viewStatsButton);
        openSettingsButton = findViewById(R.id.openSettingsButton); // nowy
    }

    private void initDatabase() {
        db = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, "task-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries() // ❗️Zalecane do zmiany na asynchroniczne
                .build();
    }

    private void setupListeners() {
        addTaskButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddTaskActivity.class)));

        viewStatsButton.setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class)));

        openSettingsButton.setOnClickListener(v -> // nowy
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void loadTasks() {
        List<Task> taskList = db.taskDao().getAllTasks();
        taskAdapter = new TaskAdapter(taskList, this, db);
        recyclerView.setAdapter(taskAdapter);
    }
}
