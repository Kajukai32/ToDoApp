package com.arturojas32.todoapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.arturojas32.todoapp.R

@Composable
fun MyPasswordTextField(
    value: String,
    passwordVisibility: Boolean = true,
    onValueChange: (String) -> Unit,
    onPasswordVisibilityClick: () -> Unit = {},
    isEnabled: Boolean = true,
) {

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (!passwordVisibility) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable { onPasswordVisibilityClick() },
                painter = if (passwordVisibility) {
                    painterResource(R.drawable.ic_visibility_off)
                } else {
                    painterResource(R.drawable.ic_visibility_on)
                },
                contentDescription = "password visibility clickable icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }, enabled = isEnabled,
        label = {
            Text(text = "Password")
        }
    )
}