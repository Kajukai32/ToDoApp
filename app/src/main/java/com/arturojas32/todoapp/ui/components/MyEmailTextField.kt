package com.arturojas32.todoapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun MyEmailTextField(value: String, onValueChange: (String) -> Unit, isEnabled: Boolean = true) {

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Email
        ),
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
        }, enabled = isEnabled,
        label = {
            Text(text = "Email")
        }
    )
}