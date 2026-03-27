package com.arturojas32.todoapp.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyDropDownItem(modifier: Modifier = Modifier, optionText: String, onClick: () -> Unit) {

    DropdownMenuItem(
        text = { Text(text = optionText) },
        onClick = { onClick() },
        contentPadding = PaddingValues(16.dp)
    )
}