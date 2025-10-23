package com.example.timepal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskStepAdapter extends RecyclerView.Adapter<TaskStepAdapter.StepViewHolder> {

    private List<TaskStep> stepList;
    private Context context;
    private TaskDatabase db;
    private Runnable onStepChanged;

    public TaskStepAdapter(List<TaskStep> stepList, Context context, TaskDatabase db, Runnable onStepChanged) {
        this.stepList = stepList;
        this.context = context;
        this.db = db;
        this.onStepChanged = onStepChanged;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        TaskStep step = stepList.get(position);
        holder.checkBox.setText(step.stepText);
        holder.checkBox.setChecked(step.completed);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            step.completed = isChecked;
            db.taskStepDao().updateStep(step);
            onStepChanged.run(); // aktualizacja postępu
        });
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.stepCheckBox);
        }
    }
}
