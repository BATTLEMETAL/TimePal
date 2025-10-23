package com.example.timepal;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class, TaskStep.class}, version = 2)
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract TaskStepDao taskStepDao();
}
