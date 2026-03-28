package com.arturojas32.todoapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arturojas32.todoapp.ui.viewmodels.LoginScreenViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginScreenViewModel: LoginScreenViewModel = hiltViewModel(), onLoginClick: () -> Unit
) {

    val loginScreenUIState by loginScreenViewModel.loginScreenUiState.collectAsStateWithLifecycle()

    Scaffold() { innerpading ->
        Column(
            modifier = modifier
                .padding(paddingValues = innerpading)
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                2.dp,
                alignment = Alignment.CenterVertically
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = modifier.weight(1f))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = loginScreenUIState.user,
                onValueChange = { newValue ->
                    loginScreenViewModel.onUserTextFieldValueChange(
                        newValue
                    )
                },
                label = {
                    Text(text = "Username")
                }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = loginScreenUIState.password,
                onValueChange = { newValue ->
                    loginScreenViewModel.onPasswordTextFieldValueChange(
                        newValue
                    )
                },
                label = {
                    Text(text = "Password")
                }
            )
            Spacer(modifier = modifier.weight(1f))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = loginScreenUIState.isLoginButtonEnabled,
                onClick = {
                    loginScreenViewModel.onLoginClick()
                    if (loginScreenUIState.wasLoginSuccessful) {
                        onLoginClick()
                    }
                }, shape = RoundedCornerShape(16)
            ) {
                Text(text = "Log in")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}, shape = RoundedCornerShape(16)
            ) {
                Text(text = "Register")
            }
            Spacer(modifier = modifier.height(16.dp))

        }
    }
}