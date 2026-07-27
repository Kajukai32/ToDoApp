package com.arturojas32.todoapp.domain.model

data class Task(
    val id: Int = 0,
    val uId: String = "123",
    val remoteId: String? = null,
    val title: String = "",
    val desc: String? = null,
    val isDone: Boolean = false,
    val createdDate: String = "",
    val deadLine: String? = null,
    val lastModified: Long = 0L,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
)
