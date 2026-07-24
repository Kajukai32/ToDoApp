package com.arturojas32.todoapp.ui

import com.arturojas32.todoapp.domain.model.Task
import com.arturojas32.todoapp.domain.repository.RemoteDbRepository

class FakeRemoteDbRepository : RemoteDbRepository {

    var synchronizationCalled = false

    override suspend fun synchronization() {
        synchronizationCalled = true
    }

    override suspend fun uploadTask(task: Task): Result<String> = Result.success("fake-remote-id")
}
