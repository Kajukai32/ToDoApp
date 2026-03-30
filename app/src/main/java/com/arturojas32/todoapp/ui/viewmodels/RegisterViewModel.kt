package com.arturojas32.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturojas32.todoapp.data.network.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val userEmail: String = "",
    val password: String = ""
)

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {

    private val _registerUiState = MutableStateFlow<RegisterUiState>(
        value = RegisterUiState()
    )
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState

    fun register() {

        viewModelScope.launch {
            _registerUiState.update { currentState ->
                currentState.copy(loading = true, error = null)
            }

            val r = repo.register(
                email = _registerUiState.value.userEmail.trim(),
                password = _registerUiState.value.password
            )

            _registerUiState.update { currentState ->
                currentState.copy(loading = false, error = r.exceptionOrNull()?.toReadable())

            }
        }

    }

}
private fun Throwable.toReadable(): String {
    return (this.message ?: "Unexpected error. Try again later")
}