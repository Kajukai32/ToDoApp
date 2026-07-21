package com.arturojas32.todoapp.domain.model


data class Task(
    val id: Int = 0,
    val uId: String,
    val remoteId: String? = null,
    val title: String,
    val desc: String? = null,
    val isDone: Boolean = false,
    val createdDate: String,
    val deadLine: String? = null,
    val lastModified: Long,
    val isSynced: Boolean = false

)