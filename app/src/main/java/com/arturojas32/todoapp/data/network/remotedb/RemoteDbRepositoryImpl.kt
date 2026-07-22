package com.arturojas32.todoapp.data.network.remotedb

import android.util.Log
import com.arturojas32.todoapp.data.network.auth.data.AuthRepository
import com.arturojas32.todoapp.domain.model.Task
import com.arturojas32.todoapp.domain.repository.TaskRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDbRepositoryImpl @Inject constructor(
    private val localTaskRepo: TaskRepository,
    private val authRepo: AuthRepository
) :
    RemoteDbRepository {

    private val realTimeDB: FirebaseDatabase = Firebase.database
    private val uId: String? get() = authRepo.currentUser()?.uid

    private fun getUserTasksRef() = uId?.let { uid ->
        realTimeDB.getReference("users").child(uid).child("tasks")
    }

    override suspend fun synchronization() {
        val unSyncedSavedTasks = localTaskRepo.getUnsyncedTasks()
//task synced from local
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
//task synced from remote
            val snapshot = getUserTasksRef()?.get()?.await()
            snapshot?.children?.forEach { children ->
                val remoteTask = children.getValue(Task::class.java)
                if (remoteTask?.remoteId != null) {
                    val localTask = localTaskRepo.getTaskByRemoteId(remoteTask.remoteId)
                    if (localTask == null) {
                        localTaskRepo.insertTask(remoteTask.copy(id = 0, isSynced = true))
                    } else if (remoteTask.lastModified > localTask.lastModified) {
                        localTaskRepo.insertTask(
                            remoteTask.copy(
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

    override fun getTasksFromRemote(): Flow<DataSnapshot> = callbackFlow {

        val ref = getUserTasksRef() ?: run {
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(ds: DataSnapshot) {
                trySend(
                    element = ds
                ).isSuccess

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("FirebaseRTDB", "get task from remote cancelled")
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun uploadTask(task: Task): Result<String> = runCatching {
        val ref = getUserTasksRef() ?: throw Exception("User not authenticated")

        val taskRef = task.remoteId?.let { remoteId -> ref.child(remoteId) } ?: ref.push()
        val generatedKey = taskRef.key ?: throw Exception("could not generate the key")

        val taskToUpload = task.copy(remoteId = generatedKey, isSynced = true)
        taskRef.setValue(taskToUpload).await()
        generatedKey
    }
}