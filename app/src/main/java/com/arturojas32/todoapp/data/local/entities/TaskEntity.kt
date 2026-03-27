package com.arturojas32.todoapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val desc: String? = null,
    val isDone: Boolean = false,
    val createdDate: String,
    val deadLine: String? = null
)