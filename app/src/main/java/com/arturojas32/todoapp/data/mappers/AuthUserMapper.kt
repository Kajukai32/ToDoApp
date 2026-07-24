package com.arturojas32.todoapp.data.mappers

import com.arturojas32.todoapp.domain.model.AuthUser
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toAuthUser() = AuthUser(
    uId = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl?.toString()
)
