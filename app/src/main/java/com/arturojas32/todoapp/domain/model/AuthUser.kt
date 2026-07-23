package com.arturojas32.todoapp.domain.model

data class AuthUser(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null
)
