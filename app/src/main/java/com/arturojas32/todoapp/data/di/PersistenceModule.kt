package com.arturojas32.todoapp.data.di

import android.app.Application
import androidx.room.Room
import com.arturojas32.todoapp.data.local.dao.TaskDao
import com.arturojas32.todoapp.data.local.database.TaskDataBase
import com.arturojas32.todoapp.data.local.repository.TaskRepositoryImpl
import com.arturojas32.todoapp.domain.repository.AuthRepository
import com.arturojas32.todoapp.data.network.remotedb.RemoteDbRepositoryImpl
import com.arturojas32.todoapp.domain.repository.RemoteDbRepository
import com.arturojas32.todoapp.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

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

    @Provides
    @Singleton
    fun providesLocaTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }

    @Provides
    @Singleton
    fun providesRemoteTaskRepository(taskRepository: TaskRepository, authRepo: AuthRepository): RemoteDbRepository {
        return RemoteDbRepositoryImpl(
            localTaskRepo = taskRepository,
            authRepo = authRepo
        )
    }
}


