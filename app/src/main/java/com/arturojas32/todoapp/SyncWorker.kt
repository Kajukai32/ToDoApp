package com.arturojas32.todoapp

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arturojas32.todoapp.domain.repository.RemoteDbRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteDbRepository: RemoteDbRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            remoteDbRepository.synchronization()
            Log.i("SyncWorker", "Synchronization finished successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Synchronization failed: ${e.message}")
            Result.retry() // Tells WorkManager to try again later
        }
    }

}
