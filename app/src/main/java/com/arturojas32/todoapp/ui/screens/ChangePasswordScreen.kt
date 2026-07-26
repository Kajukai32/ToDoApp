package com.arturojas32.todoapp.ui.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arturojas32.todoapp.R
import com.arturojas32.todoapp.ui.components.MyAnimatedConfirmButton
import com.arturojas32.todoapp.ui.components.MyEmailTextField
import com.arturojas32.todoapp.ui.components.MyPasswordTextField
import com.arturojas32.todoapp.ui.components.MyTopBar
import com.arturojas32.todoapp.ui.viewmodels.ChangePasswordViewModel
import com.arturojas32.todoapp.ui.viewmodels.ForgotPasswordEvent
import com.arturojas32.todoapp.ui.viewmodels.PasswordMode
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    vm: ChangePasswordViewModel = hiltViewModel(),
    mode: PasswordMode = PasswordMode.RESET,
    onBackClick: () -> Unit,
    onNavigateToLoginAndResetBackStack: () -> Unit
) {

    val uiState = vm.forgotPasswordUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(mode) {
        vm.setMode(mode)
    }

    LaunchedEffect(Unit) {
        vm.events.collectLatest { eff ->
            when (eff) {
                is ForgotPasswordEvent.ResetSent -> {
                    Toast.makeText(
                        context,
                        "Reset link sent to your email, check your inbox.",
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateToLoginAndResetBackStack()
                }

                is ForgotPasswordEvent.ShowMessage -> {
                    Toast.makeText(context, eff.message, Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    val title = when (mode) {
        PasswordMode.RESET -> "Reset your password"
        PasswordMode.CHANGE -> "Change your password"
    }

    val buttonText = when (mode) {
        PasswordMode.RESET -> "Reset"
        PasswordMode.CHANGE -> "Confirm"
    }

    Scaffold(topBar = {
        MyTopBar(
            onBackClick = { onBackClick() },
            title = title
        )
    }) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
                .padding(16.dp)
                .imePadding()
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(
                2.dp,
                alignment = Alignment.CenterVertically
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            uiState.value.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Image(
                painter = painterResource(id = R.drawable.password_screen),
                contentDescription = "password screen identifier image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            MyEmailTextField(
                value = uiState.value.email,
                onValueChange = { newValue -> vm.onEmailValueChange(newValue) },
                isEnabled = uiState.value.areTextFieldsEnabled
            )

            if (mode == PasswordMode.CHANGE) {
                //current password
                MyPasswordTextField(
                    hintText = "Current password",
                    value = uiState.value.currentPasswordField,
                    passwordVisibility = uiState.value.currentPasswordFieldVisibility,
                    onValueChange = { newValue -> vm.onCurrentPasswordFieldValueChange(newValue) },
                    onPasswordVisibilityClick = { vm.onCurrentPasswordVisibilityChange(!uiState.value.currentPasswordFieldVisibility) },
                    isEnabled = uiState.value.areTextFieldsEnabled
                )
                //new password
                MyPasswordTextField(
                    hintText = "New password",
                    value = uiState.value.newPasswordField,
                    passwordVisibility = uiState.value.newPasswordFieldVisibility,
                    onValueChange = { newValue -> vm.onNewPasswordFieldValueChange(newValue) },
                    onPasswordVisibilityClick = { vm.onNewPasswordVisibilityChange(!uiState.value.newPasswordFieldVisibility) },
                    isEnabled = uiState.value.areTextFieldsEnabled
                )
            }

            Spacer(modifier = modifier.weight(0.7f))
            MyAnimatedConfirmButton(
                isLoading = uiState.value.loading,
                isButtonEnabled = uiState.value.enableConfirmButton,
                onConfirmClick = { vm.sendReset() },
                text = buttonText
            )
        }

    }
}
