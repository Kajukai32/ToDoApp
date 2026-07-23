package com.arturojas32.todoapp.domain.repository

import com.arturojas32.todoapp.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val authState: Flow<AuthUser?>

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun register(email: String, password: String): Result<Unit>

    fun signOut()

    fun currentUser(): AuthUser?
}
