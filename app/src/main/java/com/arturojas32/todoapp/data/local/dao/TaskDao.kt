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

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND uId = :uId ORDER BY id DESC")
    fun getAllTasks(uId: String): Flow<List<TaskEntity>>
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks1(): Flow<List<TaskEntity>>
    @Delete
    suspend fun deleteTask(taskToDelete: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query(value = "SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query(value = "DELETE  FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)


    @Query(value = "SELECT *  FROM tasks WHERE isSynced = 0")
    suspend fun getUnsyncedTasks(): List<TaskEntity>

    @Query(value = "SELECT *  FROM tasks WHERE remoteId = :remoteTaskId")
    suspend fun getTaskByRemoteId(remoteTaskId: String): TaskEntity?

    @Query(value = "SELECT * FROM tasks WHERE title LIKE '%'|| :query|| '%' OR `desc` LIKE '%'|| :query|| '%'")
    suspend fun getTasksByTitleOrAsc(query: String): List<TaskEntity>

}
