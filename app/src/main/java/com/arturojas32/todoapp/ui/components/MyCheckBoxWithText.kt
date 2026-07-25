package com.arturojas32.todoapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontStyle

@Composable
fun MyCheckBoxWithText(isChecked: Boolean, onCheckedValueChange: (Boolean) -> Unit, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { checkBoxValue -> onCheckedValueChange(checkBoxValue) },
            enabled = true,
            colors = CheckboxDefaults.colors()
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.primary
        )
    }
}