package com.arturojas32.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturojas32.todoapp.data.network.auth.data.AuthRepository
import com.arturojas32.todoapp.utils.emailAndPasswordValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {

    private val _registerUiState = MutableStateFlow<RegisterUiState>(
        value = RegisterUiState()
    )
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState

    //replay 0 = emite los ultimos 0 eventos, osea que no guarda nada, si no hay nadie escuchando la informacion se pierde y punto
    //Shared = varaias pantallas pueden estar observando
    private val _event = MutableSharedFlow<RegisterEvent>(replay = 0)
    val event: SharedFlow<RegisterEvent> = _event.asSharedFlow()

    fun register() {

        viewModelScope.launch {
            //set del registro, inhabilitando el boton una vez presionado y borrando lo que haya en el error
            _registerUiState.update { currentState ->
                currentState.copy(loading = true, error = null)
            }
            //se llama al repo para hacer efectivo el registro(o intentarlo al menos)
            val r = repo.register(
                email = _registerUiState.value.newUserEmail.trim(),
                password = _registerUiState.value.newUserPassword
            )
            //si funciono se emite un success y se habilita el nav, pero si no el error se carga en error y se muestra en pantalla
            if (r.isSuccess) {
                _registerUiState.update { currentState ->
                    currentState.copy(loading = false, error = null)
                }
                _event.emit(RegisterEvent.Success)
            } else {
                _registerUiState.update { currentState ->
                    currentState.copy(loading = false, error = r.exceptionOrNull()?.toReadable())

                }
            }
        }

    }

    fun onEmailValueChange(newValue: String) {

        _registerUiState.update { currentState ->
            currentState.copy(newUserEmail = newValue)
        }


    }

    fun onPasswordValueChange(newValue: String) {

        _registerUiState.update { currentState ->
            currentState.copy(newUserPassword = newValue)
        }
        checkValidCredentials()
    }

    fun onPasswordVisibilityChange() {

        _registerUiState.update { currentState ->
            currentState.copy(passwordVisibility = !currentState.passwordVisibility)
        }
        checkValidCredentials()
    }

    fun checkValidCredentials() {
        _registerUiState.update { currentState ->
            currentState.copy(
                isRegisterButtonEnabled = emailAndPasswordValidator(
                    email = currentState.newUserEmail,
                    password = currentState.newUserPassword
                ) && !currentState.loading
            )
        }
    }


}

private fun Throwable.toReadable(): String {
    return (this.message ?: "Unexpected error. Try again later")
}


sealed interface RegisterEvent {
    data object Success : RegisterEvent
}

data class RegisterUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val newUserEmail: String = "",
    val passwordVisibility: Boolean = true,
    val newUserPassword: String = "",
    val isRegisterButtonEnabled: Boolean = false,

)