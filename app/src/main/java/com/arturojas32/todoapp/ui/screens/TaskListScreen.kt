package com.arturojas32.todoapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arturojas32.todoapp.R
import com.arturojas32.todoapp.ui.components.MyTaskItem
import com.arturojas32.todoapp.ui.components.MyTopBar
import com.arturojas32.todoapp.ui.viewmodels.SortedBy
import com.arturojas32.todoapp.ui.viewmodels.TaskFeaturesViewModel
import com.arturojas32.todoapp.ui.viewmodels.TaskListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    taskListViewModel: TaskListViewModel = hiltViewModel(),
    addTaskListViewModel: TaskFeaturesViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit,
    onTaskItemClick: (Int) -> Unit
) {

    val taskUiState by taskListViewModel.taskListUiSate.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MyTopBar(
                onBackClick = { taskListViewModel.onSortedByChange(SortedBy.COMPLETED) },
                title = "Tasks"
            )
        },
        floatingActionButton = {
            if (!showDialog) {
                FabDialog(onClick = { onAddTaskClick() })
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay
    ) { innerpadding ->

        if (taskUiState.tasksState.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues = innerpadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center,
                content = { Text(text = "You haven't added any task yet, try adding via '+'") }
            )

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(innerpadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(taskUiState.tasksState) { task ->

                    MyTaskItem(
                        task = task,
                        onTaskItemClick = { taskId -> onTaskItemClick(taskId) },
                        onCheckedChangeClick = { taskId ->
                            addTaskListViewModel.onCheckedChangeClick(
                                taskId = taskId
                            )
                        }, onDeleteClick = { taskId -> addTaskListViewModel.onDeleteClick(taskId) })
                }

            }
        }
    }
}

@Composable
fun FabDialog(onClick: () -> Unit) {

    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        onClick = { onClick() },
        content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "add task icon"
            )
        }
    )
}

