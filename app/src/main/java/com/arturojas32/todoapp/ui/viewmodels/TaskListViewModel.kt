package com.arturojas32.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturojas32.todoapp.data.local.repository.TaskRepository
import com.arturojas32.todoapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(private val repo: TaskRepository) : ViewModel() {

    private val _tasksListUiState = MutableStateFlow<TaskListUiSate>(TaskListUiSate())
    val taskListUiSate: StateFlow<TaskListUiSate> = _tasksListUiState


    init {
        viewModelScope.launch {

            repo.getAllTasks().collect { tasksFromDB ->
                _tasksListUiState.update { currentState ->

                    val sortedList = when (currentState.sortedBy) {
                        SortedBy.COMPLETED -> {
                            tasksFromDB.sortedByDescending { task -> task.isDone }
                        }

                        SortedBy.DEFAULT -> tasksFromDB
                    }
                    currentState.copy(tasksState = sortedList, rawTaskList = tasksFromDB)
                }
            }
        }
    }

    fun onSortedByChange(sortedByNewValue: SortedBy) {
        _tasksListUiState.update { currentSate ->

            currentSate.copy(
                sortedBy = sortedByNewValue,
                tasksState = when (sortedByNewValue) {
                    SortedBy.COMPLETED -> currentSate.tasksState.sortedByDescending { task -> task.isDone }
                    SortedBy.DEFAULT -> currentSate.tasksState.sortedByDescending { task -> task.id }
                })


        }

    }

    fun onSearchFieldValueChange(newValue: String) {

        _tasksListUiState.update { currentState ->

            currentState.copy(
                stringToSearch = newValue,
                tasksState = if (newValue.isEmpty()) {
                    currentState.rawTaskList
                } else {
                    currentState.rawTaskList.filter { task ->
                        task.title.contains(
                            ignoreCase = true,
                            other = newValue
                        ) || (!task.desc.isNullOrEmpty() && task.desc.contains(
                            other =
                                newValue, ignoreCase = true
                        ))
                    }

                }
            )
        }
    }
}

data class TaskListUiSate(
    val rawTaskList: List<Task> = listOf(),
    val tasksState: List<Task> = listOf(),
//    val filteredTasksState: List<Task> = listOf(),
    val sortedBy: SortedBy = SortedBy.DEFAULT,
    val stringToSearch: String = ""
)

enum class SortedBy { COMPLETED, DEFAULT }
