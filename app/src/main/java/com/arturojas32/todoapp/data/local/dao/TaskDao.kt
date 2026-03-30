package com.arturojas32.todoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arturojas32.todoapp.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(newTask: TaskEntity)

    @Query(value = "SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Delete
    suspend fun deleteTask(taskToDelete: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query(value = "SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query(value = "DELETE  FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    @Query(value = "SELECT * FROM tasks WHERE title LIKE '%'|| :query|| '%' OR `desc` LIKE '%'|| :query|| '%'")
    suspend fun getTasksByTitleOrDesc(query: String): List<TaskEntity>

}