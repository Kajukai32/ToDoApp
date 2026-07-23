package com.arturojas32.todoapp.ui

import com.arturojas32.todoapp.data.network.remotedb.RemoteDbRepository
import com.arturojas32.todoapp.domain.model.Task
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeRemoteDbRepository : RemoteDbRepository {

    var synchronizationCalled = false

    private val _remoteTasks = MutableSharedFlow<DataSnapshot>(replay = 0)

    override suspend fun synchronization() {
        synchronizationCalled = true
    }

    override fun getTasksFromRemote(): Flow<DataSnapshot> = _remoteTasks

    override suspend fun uploadTask(task: Task): Result<String> = Result.success("fake-remote-id")
}
