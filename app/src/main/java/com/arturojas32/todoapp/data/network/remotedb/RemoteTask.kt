package com.arturojas32.todoapp.data.network.remotedb

data class RemoteTask(
    val id: Int = 0,
    val uid: String = "",
    val remoteId: String? = null,
    val title: String = "",
    val desc: String? = null,
    val done: Boolean = false,
    val createdDate: String = "",
    val deadLine: String? = null,
    val lastModified: Long = 0L,
    val synced: Boolean = false,
    val deleted: Boolean = false
)
