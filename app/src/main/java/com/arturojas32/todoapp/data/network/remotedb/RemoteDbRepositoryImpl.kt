package com.arturojas32.todoapp.data.network.remotedb

import android.util.Log
import com.arturojas32.todoapp.data.mappers.toDomain
import com.arturojas32.todoapp.data.mappers.toRemote
import com.arturojas32.todoapp.domain.repository.AuthRepository
import com.arturojas32.todoapp.domain.model.Task
import com.arturojas32.todoapp.domain.repository.RemoteDbRepository
import com.arturojas32.todoapp.domain.repository.TaskRepository
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDbRepositoryImpl @Inject constructor(
    private val localTaskRepo: TaskRepository,
    private val authRepo: AuthRepository
) :
    RemoteDbRepository {

    private val realTimeDB: FirebaseDatabase = Firebase.database
    private val uId: String? get() = authRepo.currentUser()?.uId

    private fun getUserTasksRef() = uId?.let { uId ->
        realTimeDB.getReference("users").child(uId).child("tasks")
    }

    override suspend fun synchronization() {
        val unSyncedSavedTasks = localTaskRepo.getUnsyncedTasks()
        unSyncedSavedTasks.forEach { task ->
            if (task.isDeleted) {
                task.remoteId?.let { remoteId ->
                    try {
                        getUserTasksRef()?.child(remoteId)?.removeValue()?.await()
                        localTaskRepo.deleteTaskById(task.id)
                    } catch (e: Exception) {
                        Log.i("FirebaseRTDB", "Failed to remove task in remote db")
                    }
                } ?: localTaskRepo.deleteTaskById(task.id)
            } else {
                uploadTask(task).onSuccess { remoteId ->
                    localTaskRepo.insertTask(
                        task.copy(remoteId = remoteId, isSynced = true)
                    )
                }
            }
        }
        try {
            val snapshot = getUserTasksRef()?.get()?.await()
            snapshot?.children?.forEach { children ->
                val remoteTask = children.getValue(RemoteTask::class.java)
                if (remoteTask?.remoteId != null) {
                    val task = remoteTask.toDomain()
                    val localTask = localTaskRepo.getTaskByRemoteId(task.remoteId!!)
                    if (localTask == null) {
                        localTaskRepo.insertTask(task.copy(id = 0, isSynced = true))
                    } else if (task.lastModified > localTask.lastModified) {
                        localTaskRepo.insertTask(
                            task.copy(
                                id = localTask.id,
                                isSynced = true
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.i("FirebaseRTDB", "synchronization cancelled")
        }

    }

    override suspend fun uploadTask(task: Task): Result<String> = runCatching {
        val ref = getUserTasksRef() ?: throw Exception("User not authenticated")

        val taskRef = task.remoteId?.let { remoteId -> ref.child(remoteId) } ?: ref.push()
        val generatedKey = taskRef.key ?: throw Exception("could not generate the key")

        val taskToUpload = task.copy(remoteId = generatedKey, isSynced = true).toRemote()
        taskRef.setValue(taskToUpload).await()
        generatedKey
    }
}
