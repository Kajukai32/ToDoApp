package com.arturojas32.todoapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arturojas32.todoapp.data.local.dao.TaskDao
import com.arturojas32.todoapp.data.local.entities.TaskEntity

@Database(version = 2, entities = [TaskEntity::class], exportSchema = false)
abstract class TaskDataBase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

}
