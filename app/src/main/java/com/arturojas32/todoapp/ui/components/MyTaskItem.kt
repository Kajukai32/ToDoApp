package com.arturojas32.todoapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arturojas32.todoapp.R
import com.arturojas32.todoapp.domain.model.Task

@Composable
fun MyTaskItem(
    task: Task,
    onTaskItemClick: (Int) -> Unit,
    onCheckedChangeClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {

    ElevatedCard(
        modifier = Modifier.clickable { onTaskItemClick(task.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                4.dp,
                alignment = Alignment.CenterVertically
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {


            MyCustomRow {
                Text(
                    text = "Title: ",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(modifier = Modifier, text = task.title)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(horizontal = 4.dp)
                        .clickable { onDeleteClick(task.id) },
                    painter = painterResource(R.drawable.ic_trash_can),
                    contentDescription = "delete task icon",
                    tint = MaterialTheme.colorScheme.error
                )
                Icon(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(42.dp)
                        .clickable { onTaskItemClick(task.id) },
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = "edit task icon"
                )
            }
            MyCustomRow {
                Text(
                    text = "Description: ",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = task.desc ?: "No description",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            MyCustomRow {
                Text(
                    text = "Created date: ",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(text = task.createdDate)
            }
            MyCustomRow {
                Text(
                    text = "Must be done at: ",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(text = task.deadLine ?: "Undefined")
            }
            MyCustomRow {
                Checkbox(
                    modifier = Modifier,
                    checked = task.isDone,
                    onCheckedChange = { onCheckedChangeClick(task.id) })
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
fun MyCustomRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}