package com.arturojas32.todoapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arturojas32.todoapp.ui.components.MyEmailTextField
import com.arturojas32.todoapp.ui.components.MyPasswordTextField
import com.arturojas32.todoapp.ui.components.MyTopBar
import com.arturojas32.todoapp.ui.viewmodels.RegisterEvent
import com.arturojas32.todoapp.ui.viewmodels.RegisterViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    registerViewModel: RegisterViewModel = hiltViewModel(),
    onRegistered: () -> Unit
) {

    val registerUiState by registerViewModel.registerUiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        registerViewModel.event.collect { registerEvent ->
            if (registerEvent is RegisterEvent.Success) {
                onRegistered()
            }

        }
    }
    Scaffold(
        modifier = Modifier,
        topBar = {
            MyTopBar(
                onBackClick = { onBackClick() },
                title = "Register screen",
                rightIconAction = {}
            )
        }) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.weight(1.75f))

            MyEmailTextField(
                value = registerUiState.newUserEmail,
                onValueChange = { newValue -> registerViewModel.onEmailValueChange(newValue = newValue) },
                isEnabled = !registerUiState.loading
            )
            MyPasswordTextField(
                value = registerUiState.newUserPassword,
                passwordVisibility = registerUiState.passwordVisibility,
                onValueChange = { newValue -> registerViewModel.onPasswordValueChange(newValue = newValue) },
                onPasswordVisibilityClick = { registerViewModel.onPasswordVisibilityChange() },
                isEnabled = !registerUiState.loading
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12),
                enabled = registerUiState.isRegisterButtonEnabled,
                onClick = {
                    registerViewModel.register()
                }
            ) {
                if (registerUiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        trackColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = "Create new user")
                }


            }
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12),
                onClick = { onBackClick() }
            ) { Text(text = "Go back") }

            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}