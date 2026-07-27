package com.arturojas32.todoapp.data.network.auth.data

import com.arturojas32.todoapp.data.local.database.DataStoreManager
import com.arturojas32.todoapp.data.mappers.toAuthUser
import com.arturojas32.todoapp.domain.model.AuthUser
import com.arturojas32.todoapp.domain.repository.AuthRepository
import com.arturojas32.todoapp.domain.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val dataStoreManager: DataStoreManager,
    private val taskRepository: TaskRepository
) : AuthRepository {

    override val authState: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa ->
            trySend(fa.currentUser?.toAuthUser())
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.toAuthUser())
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> =
        runCatching {
            auth.signInWithEmailAndPassword(email, password).await()
            Unit
        }

    override suspend fun register(
        email: String,
        password: String
    ): Result<Unit> =
        runCatching {
            auth.createUserWithEmailAndPassword(email, password).await()
            Unit
        }

    override suspend fun signOut() {
        auth.signOut()
        taskRepository.deleteAllTasks()
        dataStoreManager.clearUserId()
    }

    override suspend fun sendPassword(email: String): Result<Unit> {
        return runCatching {
            auth.useAppLanguage()
            val normalizedEmail = email.trim().lowercase()
            require(normalizedEmail.isNotBlank()) { "Email cannot be blank" }
            auth.sendPasswordResetEmail(normalizedEmail).await()
            Unit
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = runCatching {
        val user = auth.currentUser
            ?: throw IllegalStateException("No authenticated user")

        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
        user.reauthenticate(credential).await()
        user.updatePassword(newPassword).await()
        Unit
    }

    override fun currentUser(): AuthUser? {
        return auth.currentUser?.toAuthUser()
    }

    override suspend fun saveUserId(userId: String) {
        dataStoreManager.saveUserId(userId)
    }

    override suspend fun saveThemeMode(themeMode: Boolean) {
        dataStoreManager.saveThemeModeKey(themeMode)
    }

    override fun getUserId(): Flow<String> {
        return dataStoreManager.getUserId()
    }

    override fun getThemeMode(): Flow<Boolean> {
        return dataStoreManager.getDarkModePref()
    }
}
