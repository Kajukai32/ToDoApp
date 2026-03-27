package com.arturojas32.todoapp.data.mappers

import com.arturojas32.todoapp.data.local.entities.TaskEntity
import com.arturojas32.todoapp.domain.model.Task

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        desc = desc,
        isDone = isDone,
        createdDate = createdDate,
        deadLine = deadLine
    )
}

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        desc = desc,
        isDone = isDone,
        createdDate = createdDate,
        deadLine = deadLine
    )
}