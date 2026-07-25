package com.arturojas32.todoapp.ui

import com.arturojas32.todoapp.domain.model.Task
import com.arturojas32.todoapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

class FakeTaskRepository : TaskRepository {

    private val tasks = MutableStateFlow<List<Task>>(emptyList())
    var insertTaskCalled = false
    var lastInsertedTask: Task? = null

    fun emitTasks(newTasks: List<Task>) {
        tasks.update { newTasks }
    }

    override fun getAllTasks(uId: String): Flow<List<Task>> = tasks
    override fun getAllTasks1(): Flow<List<Task>> {
        return flowOf(tasks.value)
    }

    override suspend fun insertTask(task: Task) {
        insertTaskCalled = true
        lastInsertedTask = task
        tasks.update { current ->
            current.filter { it.id != task.id } + task
        }
    }

    override suspend fun getTaskById(taskID: Int): Task? {
        return tasks.value.find { it.id == taskID }
    }

    override suspend fun deleteTask(taskToDelete: Task) {
        tasks.update { current -> current.filter { it.id != taskToDelete.id } }
    }

    override suspend fun deleteAllTasks() {
        tasks.update { emptyList() }
    }

    override suspend fun deleteTaskById(taskId: Int) {
        tasks.update { current -> current.filter { it.id != taskId } }
    }

    override suspend fun getTasksByTitleOrDesc(string: String): List<Task> {
        return tasks.value.filter {
            it.title.contains(string, ignoreCase = true) ||
                    (it.desc != null && it.desc.contains(string, ignoreCase = true))
        }
    }

    override suspend fun getUnsyncedTasks(): List<Task> = emptyList()

    override suspend fun getTaskByRemoteId(remoteTaskId: String): Task? = null
}
