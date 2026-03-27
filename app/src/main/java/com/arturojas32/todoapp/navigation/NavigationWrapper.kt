package com.arturojas32.todoapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arturojas32.todoapp.ui.screens.AddTaskScreen
import com.arturojas32.todoapp.ui.screens.TaskListScreen

@Composable
fun NavigationWrapper(modifier: Modifier = Modifier) {

    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TaskListRoute
    ) {

        composable<TaskListRoute> {
            TaskListScreen(
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
    }


}