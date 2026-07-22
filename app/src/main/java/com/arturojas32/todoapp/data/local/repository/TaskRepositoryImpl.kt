package com.arturojas32.todoapp.data.local.repository

import com.arturojas32.todoapp.data.local.dao.TaskDao
import com.arturojas32.todoapp.data.mappers.toDomain
import com.arturojas32.todoapp.data.mappers.toEntity
import com.arturojas32.todoapp.domain.model.Task
import com.arturojas32.todoapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TaskRepository {


    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(newTask = task.toEntity())
    }

    override suspend fun deleteTask(taskToDelete: Task) {
        taskDao.deleteTask(taskToDelete = taskToDelete.toEntity())
    }

    override suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    override fun getAllTasks(uId: String): Flow<List<Task>> {

        val tasksToDomain = taskDao.getAllTasks(uId).map { currentCollection ->
            currentCollection.map { taskEntity -> taskEntity.toDomain() }
        }
        return tasksToDomain
    }

    override suspend fun getTaskById(taskID: Int): Task? {
        val requestedTask = taskDao.getTaskById(taskId = taskID)

        return requestedTask?.toDomain()
    }

    override suspend fun deleteTaskById(taskId: Int) {
        taskDao.deleteTaskById(taskId = taskId)
    }

    override suspend fun getTasksByTitleOrDesc(string: String): List<Task> {
        val tasksToDomain = taskDao.getTasksByTitleOrDesc(query = string).map { taskEntity ->
            taskEntity.toDomain()
        }
        return tasksToDomain
    }

    override suspend fun getUnsyncedTasks(): List<Task> {
        val tasksToDomain = taskDao.getUnsyncedTasks().map { taskEntity ->
            taskEntity.toDomain()
        }
        return tasksToDomain
    }

    override suspend fun getTaskByRemoteId(remoteTaskId: String): Task? {
        return taskDao.getTaskByRemoteId(remoteTaskId = remoteTaskId)?.toDomain()
    }
}
