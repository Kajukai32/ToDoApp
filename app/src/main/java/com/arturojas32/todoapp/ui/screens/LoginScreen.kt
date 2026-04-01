package com.arturojas32.todoapp.ui.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arturojas32.todoapp.R
import com.arturojas32.todoapp.ui.components.MyEmailTextField
import com.arturojas32.todoapp.ui.components.MyPasswordTextField
import com.arturojas32.todoapp.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    onGoToRegisterScreen: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedButtonBorderColor by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary,
        targetValue = MaterialTheme.colorScheme.primaryContainer,
        animationSpec = infiniteRepeatable(
            animation = tween(2000), repeatMode = RepeatMode.Reverse
        )
    )
    val animatedButtonBorderWidtSize: Dp by infiniteTransition.animateValue(
        initialValue = 0.8.dp,
        targetValue = 3.0.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500), repeatMode = RepeatMode.Reverse
        )
    )

    val loginScreenUIState by loginViewModel.loginScreenUiState.collectAsStateWithLifecycle()

    LaunchedEffect(loginViewModel) {
        loginViewModel.event.collectLatest { event ->

            when (event) {
                LoginViewModel.Event.Success -> onLoginClick()
            }
        }
    }

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
            Spacer(modifier = modifier.weight(1.3f))

            MyEmailTextField(
                value = loginScreenUIState.email,
                onValueChange = { newValue -> loginViewModel.onUserTextFieldValueChange(newValue) },
                isEnabled = !loginScreenUIState.loading
            )
//            OutlinedTextField(
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions(
//                    capitalization = KeyboardCapitalization.None,
//                    autoCorrectEnabled = false,
//                    keyboardType = KeyboardType.Email
//                ),
//                value = loginScreenUIState.email,
//                onValueChange = { newValue ->
//                    loginViewModel.onUserTextFieldValueChange(
//                        newValue
//                    )
//                }, enabled = !loginScreenUIState.loading,
//                label = {
//                    Text(text = "Email")
//                }
//            )

            MyPasswordTextField(
                value = loginScreenUIState.password,
                passwordVisibility = loginScreenUIState.passwordVisibility,
                onValueChange = { newValue -> loginViewModel.onPasswordTextFieldValueChange(newValue = newValue) },
                onPasswordVisibilityClick = { loginViewModel.onPasswordVisibilityClick() },
                isEnabled = !loginScreenUIState.loading
            )
//            OutlinedTextField(
//                modifier = Modifier.fillMaxWidth(),
//                value = loginScreenUIState.password,
//                keyboardOptions = KeyboardOptions(
//                    capitalization = KeyboardCapitalization.None,
//                    autoCorrectEnabled = false,
//                    keyboardType = KeyboardType.Password
//                ),
//                visualTransformation = if (!loginScreenUIState.passwordVisibility) {
//                    PasswordVisualTransformation()
//                } else {
//                    VisualTransformation.None
//                },
//                onValueChange = { newValue ->
//                    loginViewModel.onPasswordTextFieldValueChange(
//                        newValue
//                    )
//                },
//                trailingIcon = {
//                    Icon(
//                        modifier = Modifier.clickable { loginViewModel.onPasswordVisibilityClick() },
//                        painter = if (loginScreenUIState.passwordVisibility) {
//                            painterResource(R.drawable.ic_visibility_off)
//                        } else {
//                            painterResource(R.drawable.ic_visibility_on)
//                        },
//                        contentDescription = null
//                    )
//                }, enabled = !loginScreenUIState.loading,
//                label = {
//                    Text(text = "Password")
//                }
//            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = loginScreenUIState.stayLoggedValue,
                    onCheckedChange = { loginViewModel.onStayLoggedValueChange() },
                    enabled = true,
                    colors = CheckboxDefaults.colors()
                )
                Text(
                    text = "Stay logged in",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = modifier.weight(0.7f))
//            if (!loginScreenUIState.wasLoginSuccessful) {
//
//                Text(
//                    text = loginScreenUIState.error ?: "",
//                    color = MaterialTheme.colorScheme.error
//                )
//            }

            loginScreenUIState.error?.let { error ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    width = if (!loginScreenUIState.loading) {
                        animatedButtonBorderWidtSize
                    } else {
                        1.dp
                    },
                    color = if (!loginScreenUIState.loading) {
                        animatedButtonBorderColor
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                ),
                enabled = loginScreenUIState.isLoginButtonEnabled,
                onClick = { loginViewModel.signIn() }, shape = RoundedCornerShape(16)
            ) {
                Text(text = "Log in")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !loginScreenUIState.loading,
                onClick = { onGoToRegisterScreen() }, shape = RoundedCornerShape(16)
            ) {
                Text(text = "Register")
            }
            Spacer(modifier = modifier.height(16.dp))

        }
    }
}