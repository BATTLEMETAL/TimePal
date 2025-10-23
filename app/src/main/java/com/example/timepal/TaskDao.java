package com.example.timepal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insertTask(Task task);

    @Insert
    long insertTaskReturnId(Task task);

    @Query("SELECT * FROM Task")
    List<Task> getAllTasks();

    @Query("SELECT * FROM Task WHERE id = :id")
    Task getTaskById(int id);

    @Delete
    void deleteTask(Task task); // ✅ Metoda do usuwania zadania
}
