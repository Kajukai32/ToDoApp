package com.arturojas32.todoapp.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.arturojas32.todoapp.R

@Composable
fun AddFabDialog(onClick: () -> Unit) {

    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        onClick = { onClick() },
        content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "add task icon"
            )
        }
    )
}