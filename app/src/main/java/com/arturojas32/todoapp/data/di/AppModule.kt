package com.arturojas32.todoapp.data.di

import android.app.Application
import androidx.room.Room
import com.arturojas32.todoapp.data.local.dao.TaskDao
import com.arturojas32.todoapp.data.local.database.TaskDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesTaskDB(context: Application): TaskDataBase {
        return Room.databaseBuilder(
            context, TaskDataBase::class.java, name = "tasks_db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    @Singleton
    fun providesTaskDao(db: TaskDataBase): TaskDao {
        return db.taskDao()
    }

}