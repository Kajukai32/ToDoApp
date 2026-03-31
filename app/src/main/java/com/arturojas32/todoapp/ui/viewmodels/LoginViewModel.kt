package com.arturojas32.todoapp.ui.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturojas32.todoapp.data.network.auth.data.AuthRepository
import com.google.firebase.auth.FirebaseUser
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


    val user: StateFlow<FirebaseUser?> = repo.authState.stateIn(
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
                isLoginButtonEnabled = !_loginScreenUiState.value.loading && Patterns.EMAIL_ADDRESS.matcher(
                    currentState.email
                )
                    .matches() && currentState.password.length > 8
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
}

private fun Throwable.toReadable(): String {
    return (this.message ?: "Unexpected error. Try again later")
}

//    fun onLoginClick() {
//
//        firebaseAuth.signInWithEmailAndPassword(
//            _loginScreenUiState.value.email.trim(),
//            _loginScreenUiState.value.password.trim()
//        ).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                _loginScreenUiState.update { currentState ->
//                    currentState.copy(wasLoginSuccessful = true)
//                }
//            } else {
//                _loginScreenUiState.update { currentState ->
//                    currentState.copy(
//                        wasLoginSuccessful = false, msgLoginError = when (task.exception) {
//                            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
//                            is FirebaseAuthInvalidUserException -> "Invalid email"
//                            else -> "Failed to log in. Try again later"
//                        }
//                    )
//                }
//            }
//        }
//    }


data class LoginScreenUiState(
    val email: String = "",
    val password: String = "",
    val isLoginButtonEnabled: Boolean = false,
    val wasLoginSuccessful: Boolean = false,
//    val msgLoginError: String = "",
    val passwordVisibility: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null
)