package com.arturojas32.todoapp.data.local.repository

import com.arturojas32.todoapp.data.local.dao.TaskDao
import com.arturojas32.todoapp.data.mappers.toDomain
import com.arturojas32.todoapp.data.mappers.toEntity
import com.arturojas32.todoapp.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {


    suspend fun insertTask(task: Task) {
        taskDao.insertTask(newTask = task.toEntity())
    }

    suspend fun updateTask(updatedTask: Task) {
        taskDao.updateTask(updatedTask = updatedTask.toEntity())
    }

    suspend fun deleteTask(taskToDelete: Task) {
        taskDao.deleteTask(taskToDelete = taskToDelete.toEntity())
    }

    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    fun getAllTasks(): Flow<List<Task>> {

        val tasksToDomain = taskDao.getAllTasks().map { currentCollection ->
            currentCollection.map { taskEntity -> taskEntity.toDomain() }
        }
        return tasksToDomain
    }

    suspend fun getTaskById(taskID: Int): Task? {
        val requestedTask = taskDao.getTaskById(taskId = taskID)

        return requestedTask?.toDomain()
    }

    suspend fun deleteTaskById(taskId: Int) {
        taskDao.deleteTaskById(taskId = taskId)
    }
}
