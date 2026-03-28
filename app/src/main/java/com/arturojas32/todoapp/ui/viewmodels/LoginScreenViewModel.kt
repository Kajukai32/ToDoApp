package com.arturojas32.todoapp.ui.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor() : ViewModel() {
    private val _loginScreenUiState = MutableStateFlow<LoginScreenUiState>(
        value = LoginScreenUiState()
    )

    val firebaseAuth = Firebase.auth

    val loginScreenUiState: StateFlow<LoginScreenUiState> = _loginScreenUiState
    fun onUserTextFieldValueChange(newValue: String) {
        _loginScreenUiState.update { currentState ->
            currentState.copy(user = newValue)
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
                isLoginButtonEnabled = Patterns.EMAIL_ADDRESS.matcher(currentState.user)
                    .matches() && currentState.password.length > 8
            )
        }
    }

    fun onLoginClick() {

        firebaseAuth.signInWithEmailAndPassword(
            _loginScreenUiState.value.user,
            _loginScreenUiState.value.password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _loginScreenUiState.update { currentState ->
                    currentState.copy(wasLoginSuccessful = true)
                }
            } else {
                _loginScreenUiState.update { currentState ->
                    currentState.copy(wasLoginSuccessful = false)
                }
            }
        }
    }

}

data class LoginScreenUiState(
    val user: String = "",
    val password: String = "",
    val isLoginButtonEnabled: Boolean = false,
    val wasLoginSuccessful: Boolean = false
)