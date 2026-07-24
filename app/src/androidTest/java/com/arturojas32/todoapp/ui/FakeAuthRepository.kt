package com.arturojas32.todoapp.ui

import com.arturojas32.todoapp.domain.model.AuthUser
import com.arturojas32.todoapp.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthRepository : AuthRepository {

    var signInResult: Result<Unit> = Result.success(Unit)
    var signInDelay: Long = 0
    var signInCalled: Boolean = false

    var registerResult: Result<Unit> = Result.success(Unit)
    var registerDelay: Long = 0
    var registerCalled: Boolean = false

    var lastEmail: String? = null
    var lastPassword: String? = null

    var fakeUid: String? = null

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

    override fun signOut() {}

    override fun currentUser(): AuthUser? =
        fakeUid?.let { AuthUser(uId = it) }

    fun reset() {
        signInResult = Result.success(Unit)
        signInDelay = 0
        signInCalled = false
        registerResult = Result.success(Unit)
        registerDelay = 0
        registerCalled = false
        lastEmail = null
        lastPassword = null
        fakeUid = null
    }
}
