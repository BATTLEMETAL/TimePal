package com.example.timepal;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TaskStep {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int taskId;
    public String stepText;
    public boolean completed;
}
