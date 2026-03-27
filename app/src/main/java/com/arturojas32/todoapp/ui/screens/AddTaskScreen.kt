package com.arturojas32.todoapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.arturojas32.todoapp.ui.components.MyDatePickerDialog
import com.arturojas32.todoapp.ui.components.MyTopBar
import com.arturojas32.todoapp.ui.viewmodels.TaskFeaturesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    taskFeaturesViewModel: TaskFeaturesViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {

    val addTaskUiState by taskFeaturesViewModel.taskState.collectAsStateWithLifecycle()
    var callDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MyTopBar(
                onBackClick = { onBackClick() },
                title = addTaskUiState.scaffoldTitle
            )
        }) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (callDialog) {
                MyDatePickerDialog(onSetDeadlineClick = {
                    taskFeaturesViewModel.onSetDeadLineClick(newValue = it)
                }, onCloseClick = {
                    callDialog = false
                })
            }
            val displayDeadline = if (addTaskUiState.task.deadLine.isNullOrBlank()) {
                "Tap the calendar to set a deadline"
            } else {
                addTaskUiState.task.deadLine
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = "Insert the task title",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                value = addTaskUiState.task.title,

                onValueChange = { taskFeaturesViewModel.onTitleTextFieldValueChange(it) }
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), maxLines = 5,
                label = {
                    Text(
                        text = "Insert the task description",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                value = addTaskUiState.task.desc ?: "",
                onValueChange = { taskFeaturesViewModel.onDescTextFieldValueChange(it) }
            )
            MySpacer()
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = displayDeadline!!,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    onValueChange = {})
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { callDialog = true }
                        .padding(4.dp),
                    contentDescription = "clickable calendar icon"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = addTaskUiState.saveButtonEnabled,
                onClick = {
                    taskFeaturesViewModel.onSaveTaskClick()
                    onBackClick()
                }
            ) {
                Text(text = addTaskUiState.submitButtonText)
            }
        }
    }


}

@Composable
fun ColumnScope.MySpacer() {
    Spacer(modifier = Modifier.height(8.dp))

}


























