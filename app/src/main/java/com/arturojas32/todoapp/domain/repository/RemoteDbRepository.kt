package com.arturojas32.todoapp.domain.repository

import com.arturojas32.todoapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface RemoteDbRepository {

    suspend fun synchronization()
    suspend fun uploadTask(task: Task): Result<String>
}
