package com.arturojas32.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturojas32.todoapp.domain.model.AuthUser
import com.arturojas32.todoapp.domain.repository.AuthRepository
import com.arturojas32.todoapp.utils.emailAndPasswordValidator
import com.arturojas32.todoapp.utils.toReadable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {


    sealed interface Event {
        data object Success : Event
    }

    //esto lo hacemos para los eventos de un solo uso
    private val _event = MutableSharedFlow<Event>()
    val event: SharedFlow<Event> = _event.asSharedFlow()


    val user: StateFlow<AuthUser?> = repo.authState.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null
    )
    private val _loginScreenUiState = MutableStateFlow<LoginScreenUiState>(
        value = LoginScreenUiState()
    )
    val loginScreenUiState: StateFlow<LoginScreenUiState> = _loginScreenUiState

    fun onUserTextFieldValueChange(newValue: String) {
        _loginScreenUiState.update { currentState ->
            currentState.copy(email = newValue)
        }
        checkValidFields()
    }

    fun onPasswordTextFieldValueChange(newValue: String) {
        _loginScreenUiState.update { currentState ->
            currentState.copy(password = newValue)
        }
        checkValidFields()
    }

    fun checkValidFields() {
        _loginScreenUiState.update { currentState ->
            currentState.copy(
                isLoginButtonEnabled = !_loginScreenUiState.value.loading && emailAndPasswordValidator(
                    email = _loginScreenUiState.value.email,
                    password = _loginScreenUiState.value.password
                )
            )
        }
    }

    fun onPasswordVisibilityClick() {
        _loginScreenUiState.update { currentState ->
            currentState.copy(passwordVisibility = !currentState.passwordVisibility)
        }
    }

    fun signIn() {
        viewModelScope.launch {
            _loginScreenUiState.update { currentState ->
                currentState.copy(
                    loading = true,
                    error = null
                )
            }
            val r = repo.signIn(
                email = _loginScreenUiState.value.email.trim(),
                password = _loginScreenUiState.value.password
            )
            checkValidFields()
            if (r.isSuccess) {
                _loginScreenUiState.update { currentState ->
                    currentState.copy(loading = false)
                }
                _event.emit(Event.Success)
                onLoginEventSuccess()
            } else {

                _loginScreenUiState.update { currentState ->
                    currentState.copy(
                        loading = false,
                        error = r.exceptionOrNull()?.toReadable()
                    )
                }
            }
        }
    }

    private fun onLoginEventSuccess() {
        if (_loginScreenUiState.value.stayLoggedValue) {
            viewModelScope.launch {
                repo.currentUser()?.let { user ->
                    repo.saveUserId(userId = user.uId)
                }
            }
        }
    }

    fun onStayLoggedValueChange(newValue: Boolean) {
        _loginScreenUiState.update { currentState ->
            currentState.copy(stayLoggedValue = newValue)
        }
    }
}



data class LoginScreenUiState(
    val email: String = "",
    val password: String = "",
    val isLoginButtonEnabled: Boolean = false,
    val wasLoginSuccessful: Boolean = false,
    val passwordVisibility: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val stayLoggedValue: Boolean = true
)