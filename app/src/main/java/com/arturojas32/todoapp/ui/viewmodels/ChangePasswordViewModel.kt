package com.arturojas32.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturojas32.todoapp.domain.repository.AuthRepository
import com.arturojas32.todoapp.utils.emailValidator
import com.arturojas32.todoapp.utils.passwordValidator
import com.arturojas32.todoapp.utils.toReadable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

enum class PasswordMode { RESET, CHANGE }

data class ForgotPasswordUIState(
    val mode: PasswordMode = PasswordMode.RESET,
    val email: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val currentPasswordFieldVisibility: Boolean = false,
    val newPasswordFieldVisibility: Boolean = false,
    val enableConfirmButton: Boolean = false,
    val newPasswordField: String = "",
    val currentPasswordField: String = "",
    val areTextFieldsEnabled: Boolean = !loading
)

sealed interface ForgotPasswordEvent {
    data class ShowMessage(val message: String) : ForgotPasswordEvent
    data object ResetSent : ForgotPasswordEvent
}

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    private val _forgotPasswordUiState = MutableStateFlow(ForgotPasswordUIState())
    val forgotPasswordUiState = _forgotPasswordUiState

    private val _events = MutableSharedFlow<ForgotPasswordEvent>()
    val events: SharedFlow<ForgotPasswordEvent> = _events

    fun setMode(mode: PasswordMode) {
        val currentEmail = authRepository.currentUser()?.email ?: ""
        _forgotPasswordUiState.update {
            it.copy(mode = mode, email = currentEmail)
        }
        isEnableConfirmButton()
    }

    fun onEmailValueChange(newValue: String) {
        _forgotPasswordUiState.update { currentState ->
            currentState.copy(email = newValue, error = null)
        }
        isEnableConfirmButton()
    }

    fun sendReset() {
        if (_forgotPasswordUiState.value.loading) return

        val em: String = _forgotPasswordUiState.value.email.trim().lowercase(Locale.ROOT)

        if (!emailValidator(em)) {
            _forgotPasswordUiState.update { currentState ->
                currentState.copy(error = "Invalid email")
            }
            return
        }
        _forgotPasswordUiState.update { currentState ->
            currentState.copy(loading = true, error = null)
        }
        viewModelScope.launch {
            val r = authRepository.sendPassword(em)
            if (r.isSuccess) {
                _forgotPasswordUiState.update { currentState ->
                    currentState.copy(loading = false, error = null)
                }
                _events.emit(ForgotPasswordEvent.ShowMessage(message = "Email with a link to reset your password has been sent"))
                _events.emit(ForgotPasswordEvent.ResetSent)

            } else {
                _forgotPasswordUiState.update { currentState ->
                    currentState.copy(loading = false, error = r.exceptionOrNull()?.toReadable())
                }
            }
        }
    }

    fun onCurrentPasswordFieldValueChange(newValue: String) {
        _forgotPasswordUiState.update { currentState ->
            currentState.copy(currentPasswordField = newValue)
        }
        isEnableConfirmButton()
    }

    fun onNewPasswordFieldValueChange(newValue: String) {
        _forgotPasswordUiState.update { currentState ->
            currentState.copy(newPasswordField = newValue)
        }
        isEnableConfirmButton()
    }

    fun onCurrentPasswordVisibilityChange(newValue: Boolean) {
        _forgotPasswordUiState.update { currentState ->
            currentState.copy(currentPasswordFieldVisibility = newValue)
        }
    }

    fun onNewPasswordVisibilityChange(newValue: Boolean) {
        _forgotPasswordUiState.update { currentState ->
            currentState.copy(newPasswordFieldVisibility = newValue)
        }
    }

    fun isEnableConfirmButton() {
        val state = _forgotPasswordUiState.value
        _forgotPasswordUiState.update { currentState ->
            currentState.copy(
                enableConfirmButton = when (currentState.mode) {
                    PasswordMode.RESET -> emailValidator(state.email) && !state.loading
                    PasswordMode.CHANGE -> emailValidator(state.email) &&
                            passwordValidator(state.newPasswordField) &&
                            passwordValidator(state.currentPasswordField) &&
                            !state.loading
                }
            )
        }
    }
}
