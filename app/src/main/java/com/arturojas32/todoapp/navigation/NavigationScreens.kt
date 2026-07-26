package com.arturojas32.todoapp.navigation

import kotlinx.serialization.Serializable

@Serializable
object TaskListRoute

@Serializable
object AddTaskRoute

@Serializable
data class UpdateTaskRoute(val taskId: Int)
@Serializable
object ChangePasswordRoute

@Serializable
object LoginScreenRoute

@Serializable
object RegisterScreenRoute