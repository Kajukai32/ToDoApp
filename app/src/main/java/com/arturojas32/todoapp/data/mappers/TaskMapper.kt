package com.arturojas32.todoapp.data.mappers

import com.arturojas32.todoapp.data.local.entities.TaskEntity
import com.arturojas32.todoapp.data.network.remotedb.RemoteTask
import com.arturojas32.todoapp.domain.model.Task

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        desc = desc,
        isDone = isDone,
        createdDate = createdDate,
        deadLine = deadLine,
        uId = uId,
        remoteId = remoteId,
        lastModified = lastModified,
        isSynced = isSynced,
        isDeleted = isDeleted
    )
}

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        desc = desc,
        isDone = isDone,
        createdDate = createdDate,
        deadLine = deadLine,
        uId = uId,
        remoteId = remoteId,
        lastModified = lastModified,
        isSynced = isSynced,
        isDeleted = isDeleted
    )

}

fun RemoteTask.toDomain(): Task {
    return Task(
        id = id,
        uId = uid,
        remoteId = remoteId,
        title = title,
        desc = desc,
        isDone = done,
        createdDate = createdDate,
        deadLine = deadLine,
        lastModified = lastModified,
        isSynced = synced,
        isDeleted = deleted
    )
}

fun Task.toRemote(): RemoteTask {
    return RemoteTask(
        id = id,
        uid = uId,
        remoteId = remoteId,
        title = title,
        desc = desc,
        done = isDone,
        createdDate = createdDate,
        deadLine = deadLine,
        lastModified = lastModified,
        synced = isSynced,
        deleted = isDeleted
    )
}