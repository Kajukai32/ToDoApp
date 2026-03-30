package com.arturojas32.todoapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arturojas32.todoapp.R
import com.arturojas32.todoapp.ui.components.AddFabDialog
import com.arturojas32.todoapp.ui.components.MyDropDownItem
import com.arturojas32.todoapp.ui.components.MyTaskItem
import com.arturojas32.todoapp.ui.components.MyTopBar
import com.arturojas32.todoapp.ui.viewmodels.SortedBy
import com.arturojas32.todoapp.ui.viewmodels.TaskFeaturesViewModel
import com.arturojas32.todoapp.ui.viewmodels.TaskListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    taskListViewModel: TaskListViewModel = hiltViewModel(),
    taskFeaturesViewModel: TaskFeaturesViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onTaskItemClick: (Int) -> Unit
) {

    val taskListUiState by taskListViewModel.taskListUiSate.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    var isSearchBarVisible by remember { mutableStateOf(false) }
    val snackBarState = remember { SnackbarHostState() }
    val scope: CoroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MyTopBar(
                onBackClick = { onBackClick() },
                title = "Tasks",
                rightIconAction = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable(onClick = { isExpanded = !isExpanded }),
                        contentAlignment = Alignment.Center
                    ) {
                        Row() {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { isSearchBarVisible = true },
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = "clickable search icon"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(R.drawable.ic_sort_by),
                                contentDescription = "Clickable sort tasks icon"
                            )
                            DropdownMenu(
                                expanded = isExpanded,
                                onDismissRequest = { isExpanded = false }
                            ) {
                                SortedBy.entries.forEach { option ->
                                    MyDropDownItem(
                                        optionText = when (option) {
                                            SortedBy.COMPLETED -> "Done tasks"
                                            SortedBy.DEFAULT -> "Recently created"
                                        },
                                        onClick = {
                                            taskListViewModel.onSortedByChange(sortedByNewValue = option)
                                            isExpanded = false
                                        })
                                }
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!showDialog) {
                AddFabDialog(onClick = { onAddTaskClick() })
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarState
            )
        }
    ) { innerpadding ->

        if (taskListUiState.tasksState.isEmpty() && !isSearchBarVisible) {
            Box(
                modifier = Modifier
                    .padding(paddingValues = innerpadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center,
                content = { Text(text = "You haven't added any task yet, try adding via '+'") }
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerpadding)
                    .padding(8.dp)
            ) {
                if (isSearchBarVisible) {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        label = { Text(text = "Title or description reference") },
                        value = taskListUiState.stringToSearch,
                        onValueChange = { newValue ->
                            taskListViewModel.onSearchFieldValueChange(
                                newValue = newValue
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_cancel_operation),
                                modifier = Modifier.clickable { isSearchBarVisible = false },
                                contentDescription = "clickable cancel operation icon"
                            )
                        }
                    )
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(taskListUiState.tasksState) { task ->

                        MyTaskItem(
                            task = task,
                            onTaskItemClick = { taskId -> onTaskItemClick(taskId) },
                            onCheckedChangeClick = { taskId ->
                                taskFeaturesViewModel.onCheckedChangeClick(
                                    taskId = taskId
                                )
                            },
                            onDeleteClick = { taskId ->
                                taskFeaturesViewModel.onDeleteClick(taskId)
                                scope.launch {
                                    val result = snackBarState.showSnackbar(
                                        message = "Task deleted",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Long
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        taskFeaturesViewModel.restoreTask()
                                    }
                                }
                            })
                    }
                }
            }
        }
    }
}




















