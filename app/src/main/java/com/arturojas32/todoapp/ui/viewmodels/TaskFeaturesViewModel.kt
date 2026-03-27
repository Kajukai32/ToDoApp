package com.arturojas32.todoapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.arturojas32.todoapp.data.local.repository.TaskRepository
import com.arturojas32.todoapp.domain.model.Task
import com.arturojas32.todoapp.navigation.UpdateTaskRoute
import com.arturojas32.todoapp.utils.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskFeaturesViewModel @Inject constructor(
    private val repo: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskID: Int? = try {
        savedStateHandle.toRoute<UpdateTaskRoute>().taskId
    } catch (e: Exception) {
        null
    }
    private val _taskState = MutableStateFlow<TaskState>(
        value = TaskState()
    )

    val taskState: StateFlow<TaskState> = _taskState

    init {
        if (taskID != null) {

            viewModelScope.launch {
                val fetchedTask = repo.getTaskById(taskID)

                fetchedTask?.let { task ->
                    _taskState.update { currentState ->
                        currentState.copy(
                            task = task,
                            saveButtonEnabled = true,
                            scaffoldTitle = "Edit task",
                            submitButtonText = "Update task"
                        )
                    }
                }
            }
        }
    }


    fun onTitleTextFieldValueChange(newValue: String) {

        _taskState.update { currentState ->
            currentState.copy(task = currentState.task.copy(title = newValue))
        }
        checkValidFields()
    }

    fun onDescTextFieldValueChange(newValue: String) {
        _taskState.update { currentState ->
            currentState.copy(task = currentState.task.copy(desc = newValue))
        }
    }

    fun onSetDeadLineClick(newValue: String) {
        _taskState.update { currentTaskState ->
            currentTaskState.copy(task = currentTaskState.task.copy(deadLine = newValue))
        }
    }

    fun onSaveTaskClick() {
        viewModelScope.launch {
            repo.insertTask(_taskState.value.task)
        }
    }

    private fun checkValidFields() {
        if (_taskState.value.task.title.isNotBlank()) {
            _taskState.update { currentState ->
                currentState.copy(saveButtonEnabled = true)
            }
        } else {
            _taskState.update { currentState ->
                currentState.copy(saveButtonEnabled = false)
            }
        }
    }

    fun onCheckedChangeClick(taskId: Int) {

        viewModelScope.launch {
            val fetchedTask = repo.getTaskById(taskId)
            fetchedTask?.let { fetchedTask ->
                val updatedTask = fetchedTask.copy(isDone = !fetchedTask.isDone)
                _taskState.update { currentState ->
                    currentState.copy(task = updatedTask)
                }
            }
            repo.insertTask(_taskState.value.task)
        }
    }

    fun onDeleteClick(taskId: Int) {
        viewModelScope.launch {
            repo.deleteTaskById(taskId)
        }
    }
}

data class TaskState(
    val task: Task = Task(
        id = 0,
        title = "",
        desc = null,
        isDone = false,
        createdDate = getCurrentDate(),
        deadLine = null
    ),
    val saveButtonEnabled: Boolean = false,
    val scaffoldTitle: String = "New task",
    val submitButtonText: String = "Save task"
)
