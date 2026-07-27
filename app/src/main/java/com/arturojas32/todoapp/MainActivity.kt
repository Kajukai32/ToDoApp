package com.arturojas32.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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


                if (mainUiState.isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.task_login_screen_image),
                            contentDescription = "password screen identifier image",
                            modifier = Modifier
                                .size(180.dp)
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally)
                        )


                    }
                }
                NavigationWrapper(
//                    startOnHome = if (mainUiState.userId.isNotEmpty()) true else false
                    startOnHome = mainUiState.userId.isNotEmpty()
                )
            }
        }
    }
}


@HiltViewModel
class MainViewModel @Inject constructor(private val authRepo: AuthRepository) : ViewModel() {

    private val _mainUiState = MutableStateFlow<MainUiState>(value = MainUiState())
    val mainUiState = _mainUiState


    init {
        viewModelScope.launch {
            authRepo.getUserId().collect { userId ->
                _mainUiState.value = _mainUiState.value.copy(userId = userId, isLoading = false)
            }
        }

    }
}

data class MainUiState(
    val isDarkMode: Boolean = false,
    val userId: String = "",
    val isLoading: Boolean = true
)