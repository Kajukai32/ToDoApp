package com.arturojas32.todoapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arturojas32.todoapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    onBackClick: () -> Unit,
    title: String,
    rightIconAction: @Composable RowScope.() -> Unit = {}
) {

    TopAppBar(
        modifier = Modifier,
        actions = { rightIconAction() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = { Text(text = title) },
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .clickable(onClick = { onBackClick() })
                    .padding(horizontal = 12.dp),
                painter = painterResource(R.drawable.ic_arrowback),
                contentDescription = null
            )
        })

}