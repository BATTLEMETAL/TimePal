package com.example.timepal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskStepDao {

    @Insert
    void insertStep(TaskStep step);

    @Insert
    void insertSteps(List<TaskStep> steps);

    @Query("SELECT * FROM TaskStep WHERE taskId = :taskId")
    List<TaskStep> getStepsForTask(int taskId);

    @Query("SELECT * FROM TaskStep") // ✅ Przydatne do statystyk lub debugowania
    List<TaskStep> getAllSteps();

    @Update
    void updateStep(TaskStep step);
}
