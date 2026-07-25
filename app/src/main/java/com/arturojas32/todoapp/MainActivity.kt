package com.arturojas32.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.arturojas32.todoapp.domain.repository.AuthRepository
import com.arturojas32.todoapp.navigation.NavigationWrapper
import com.arturojas32.todoapp.ui.theme.ToDoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                val mainVm: MainViewModel = hiltViewModel()
                val mainUiState by mainVm.mainUiState.collectAsStateWithLifecycle()

                NavigationWrapper(
//                    startOnHome = if (mainUiState.userId.isNotEmpty()) true else false
                    startOnHome = mainUiState.userId.isNotEmpty()
                )
            }
        }
    }
}
//cosas que falta=
//cerrar sesion, implementacion de stay logged in

@HiltViewModel
class MainViewModel @Inject constructor(private val authRepo: AuthRepository) : ViewModel() {

    private val _mainUiState = MutableStateFlow<MainUiState>(value = MainUiState())
    val mainUiState = _mainUiState


    init {
        viewModelScope.launch {
            authRepo.getUserId().collect { userId ->
                _mainUiState.value = _mainUiState.value.copy(userId = userId)
            }
        }

    }
}

data class MainUiState(
    val isDarkMode: Boolean = false,
    val userId: String = ""
)