

package com.arturojas32.todoapp.domain.repository

import com.arturojas32.todoapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insertTask(task: Task)

    suspend fun deleteTask(taskToDelete: Task)

    suspend fun deleteAllTasks()

    fun getAllTasks(uId: String): Flow<List<Task>>
    fun getAllTasks1(): Flow<List<Task>>

    suspend fun getTaskById(taskID: Int): Task?

    suspend fun deleteTaskById(taskId: Int)

    suspend fun getTasksByTitleOrDesc(string: String): List<Task>

    suspend fun getUnsyncedTasks(): List<Task>

    suspend fun getTaskByRemoteId(remoteTaskId: String): Task?
}