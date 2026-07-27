package com.arturojas32.todoapp.ui

import com.arturojas32.todoapp.domain.model.AuthUser
import com.arturojas32.todoapp.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository : AuthRepository {

    var signInResult: Result<Unit> = Result.success(Unit)
    var signInDelay: Long = 0
    var signInCalled: Boolean = false

    var registerResult: Result<Unit> = Result.success(Unit)
    var registerDelay: Long = 0
    var registerCalled: Boolean = false

    var lastEmail: String? = null
    var lastPassword: String? = null

    var changePasswordResult: Result<Unit> = Result.success(Unit)
    var changePasswordCalled: Boolean = false
    var lastCurrentPassword: String? = null
    var lastNewPassword: String? = null

    var fakeUid: String? = null
    var fakeEmail: String? = null
    var signOutCalled: Boolean = false

    private val _authState = MutableStateFlow<AuthUser?>(null)
    override val authState: Flow<AuthUser?> = _authState

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        lastEmail = email
        lastPassword = password
        signInCalled = true
        if (signInDelay > 0) delay(signInDelay)
        return signInResult
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        lastEmail = email
        lastPassword = password
        registerCalled = true
        if (registerDelay > 0) delay(registerDelay)
        return registerResult
    }

    override suspend fun signOut() {
        signOutCalled = true
        fakeUid = null
    }

    override suspend fun sendPassword(email: String): Result<Unit> {
        lastEmail = email
        return Result.success(Unit)
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        lastCurrentPassword = currentPassword
        lastNewPassword = newPassword
        changePasswordCalled = true
        return changePasswordResult
    }

    override fun currentUser(): AuthUser? =
        fakeUid?.let { AuthUser(uId = it, email = fakeEmail) }

    override suspend fun saveUserId(userId: String) {

    }

    override suspend fun saveThemeMode(themeMode: Boolean) {

    }

    override fun getUserId(): Flow<String> {
        return flowOf<String>("false")
    }

    override fun getThemeMode(): Flow<Boolean> {
        return flowOf<Boolean>(false)
    }

    fun reset() {
        signInResult = Result.success(Unit)
        signInDelay = 0
        signInCalled = false
        registerResult = Result.success(Unit)
        registerDelay = 0
        registerCalled = false
        lastEmail = null
        lastPassword = null
        changePasswordResult = Result.success(Unit)
        changePasswordCalled = false
        lastCurrentPassword = null
        lastNewPassword = null
        fakeUid = null
        fakeEmail = null
        signOutCalled = false
    }
}
