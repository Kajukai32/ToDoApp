package com.arturojas32.todoapp.ui.components

//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import com.arturojas32.todoapp.ui.viewmodels.TaskUiState
//
//@Composable
//fun MyTaskDialog(
//    modifier: Modifier = Modifier,
//    taskUiState: TaskUiState,
//    onDismissDialog: () -> Unit,
//    onValueChange: (String) -> Unit, onSaveClick: () -> Unit
//) {
//
//    Dialog(
//        onDismissRequest = { onDismissDialog() },
//    ) {
//        Box(
//            modifier = modifier
//                .clip(RoundedCornerShape(8))
//                .background(color = MaterialTheme.colorScheme.surface)
//                .fillMaxWidth()
//        )
//        {
//            Column(
//                modifier = modifier
//
//
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(
//                    16.dp,
//                    alignment = Alignment.CenterVertically
//                ), horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(text = "Introduce a new task", color = MaterialTheme.colorScheme.onSurface)
//                TextField(
//                    value = taskUiState.task, maxLines = 3,
//                    onValueChange = { onValueChange(it) }
//                )
//                Row(modifier = modifier) {
//                    TextButton(
//                        onClick = {})
//                    { Text(text = "Cancel") }
//                    Spacer(modifier = Modifier.weight(1f))
//                    Button(
//                        onClick = {onSaveClick()}
//                    ) { Text(text = "Save") }
//
//                }
//
//            }
//        }
//    }
//
//}
//@Composable
//fun MyCustomDialog(
//    modifier: Modifier = Modifier,
//    combat: PokemonCombat,
//    showDialog: Boolean,
//    onDismissDialog: () -> Unit
//) {
//
//    if (showDialog) {
//        Dialog(
//            onDismissRequest = { onDismissDialog() },
//
//            ) {
//            Column(
//                modifier = modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .background(Black), horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(text = combat.pokemonA)
//                    Text(text = " vs ")
//                    Text(text = combat.pokemonB)
//                }
//            }
//        }
//    }
//
//}