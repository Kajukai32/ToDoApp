package com.arturojas32.todoapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arturojas32.todoapp.ui.screens.AddTaskScreen
import com.arturojas32.todoapp.ui.screens.LoginScreen
import com.arturojas32.todoapp.ui.screens.RegisterScreen
import com.arturojas32.todoapp.ui.screens.TaskListScreen

@Composable
fun NavigationWrapper(modifier: Modifier = Modifier, startOnHome: Boolean) {

    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (startOnHome) TaskListRoute else LoginScreenRoute
    ) {
        composable<LoginScreenRoute> {
            LoginScreen(
                onLoginClick = { navController.navigate(TaskListRoute) },
                onGoToRegisterScreen = {
                    navController.navigate(route = RegisterScreenRoute)
                })
        }

        composable<TaskListRoute> {
            TaskListScreen(
                onBackClick = {
                    navController.navigate(route = LoginScreenRoute) {
                        popUpTo<LoginScreenRoute> { inclusive = true }
                    }
                },
                onAddTaskClick = {
                    navController.navigate(route = AddTaskRoute)
                },
            ) { taskId: Int -> navController.navigate(UpdateTaskRoute(taskId)) }
        }

        composable<AddTaskRoute> {
            AddTaskScreen(onBackClick = {
                navController.navigate(route = TaskListRoute) {
                    popUpTo<TaskListRoute> { inclusive = true }
                }
            })
        }
        composable<UpdateTaskRoute> {
            AddTaskScreen(onBackClick = {
                navController.navigate(route = TaskListRoute) {
                    popUpTo<TaskListRoute> { inclusive = true }
                }
            })
        }
        composable<RegisterScreenRoute> {

            RegisterScreen(onBackClick = {
                navController.navigate(route = LoginScreenRoute) {
                    popUpTo<LoginScreenRoute> { inclusive = true }
                }
            })
        }
    }


}