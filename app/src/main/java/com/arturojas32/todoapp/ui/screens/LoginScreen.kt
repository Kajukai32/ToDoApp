package com.arturojas32.todoapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arturojas32.todoapp.R
import com.arturojas32.todoapp.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(), onLoginClick: () -> Unit
) {

    val loginScreenUIState by authViewModel.loginScreenUiState.collectAsStateWithLifecycle()

    Scaffold() { innerpading ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues = innerpading)
                .padding(16.dp)
                .imePadding()
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(
                2.dp,
                alignment = Alignment.CenterVertically
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = modifier.weight(1f))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Email
                ),
                value = loginScreenUIState.email,
                onValueChange = { newValue ->
                    authViewModel.onUserTextFieldValueChange(
                        newValue
                    )
                },
                label = {
                    Text(text = "Email")
                }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = loginScreenUIState.password,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = if (!loginScreenUIState.passwordVisibility) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                onValueChange = { newValue ->
                    authViewModel.onPasswordTextFieldValueChange(
                        newValue
                    )
                },
                trailingIcon = {
                    Icon(
                        modifier = Modifier.clickable { authViewModel.onPasswordVisibilityClick() },
                        painter = if (loginScreenUIState.passwordVisibility) {
                            painterResource(R.drawable.ic_visibility_off)
                        } else {
                            painterResource(R.drawable.ic_visibility_on)
                        },
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Password")
                }
            )
            Spacer(modifier = modifier.weight(1f))
            if (!loginScreenUIState.wasLoginSuccessful) {

                Text(
                    text = loginScreenUIState.msgLoginError,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = loginScreenUIState.isLoginButtonEnabled,
                onClick = {
                    authViewModel.onLoginClick()
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