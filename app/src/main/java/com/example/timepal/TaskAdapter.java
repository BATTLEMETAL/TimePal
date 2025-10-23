package com.example.timepal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private final Context context;
    private final TaskDatabase db;

    public TaskAdapter(List<Task> taskList, Context context, TaskDatabase db) {
        this.taskList = taskList;
        this.context = context;
        this.db = db;
    }

    public void updateData(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.titleText.setText(task.title);
        String formattedDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                .format(new Date(task.deadlineTimestamp));
        holder.deadlineText.setText("Do: " + formattedDate);

        // Kliknięcie otwiera tryb fokusowy
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FocusModeActivity.class);
            intent.putExtra("taskId", task.id);
            context.startActivity(intent);
        });

        // Kliknięcie przycisku usuwania
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Usuń zadanie")
                    .setMessage("Czy na pewno chcesz usunąć to zadanie?")
                    .setPositiveButton("Tak", (dialog, which) -> {
                        db.taskDao().deleteTask(task);
                        taskList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Zadanie usunięte", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Nie", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, deadlineText;
        ImageButton deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.taskTitleTextView);
            deadlineText = itemView.findViewById(R.id.taskDeadlineTextView);
            deleteButton = itemView.findViewById(R.id.deleteTaskButton);
        }
    }
}
