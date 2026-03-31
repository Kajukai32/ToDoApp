package com.arturojas32.todoapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arturojas32.todoapp.ui.components.MyTopBar

@Composable
fun RegisterScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit) {

    Scaffold(modifier = Modifier, topBar = {
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
                .padding(16.dp)
        ) { }
    }


}