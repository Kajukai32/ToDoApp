package com.arturojas32.todoapp.data.network.remotedb

import com.arturojas32.todoapp.domain.model.Task
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.Flow

interface RemoteDbRepository {

    suspend fun synchronization()
    fun getTasksFromRemote(): Flow<DataSnapshot>
    suspend fun uploadTask(task: Task): Result<String>
}