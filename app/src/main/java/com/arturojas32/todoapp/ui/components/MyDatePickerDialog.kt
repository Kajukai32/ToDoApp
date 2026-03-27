package com.arturojas32.todoapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arturojas32.todoapp.utils.getSelectedDate
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onSetDeadlineClick: (String) -> Unit,
    onCloseClick: () -> Unit
) {

    val calendar: Calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, +1)
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val currentYear = calendar.get(Calendar.YEAR)

    val datePickerState = rememberDatePickerState(
        //dia seleccionado por defecto
        initialSelectedDateMillis = calendar.timeInMillis,
        yearRange = currentYear..currentYear + 2,
        initialDisplayMode = DisplayMode.Input, selectableDates =
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= today
                }
            }
    )



    DatePickerDialog(
        onDismissRequest = { onCloseClick() },
        confirmButton = {
            Button(
                modifier = Modifier.padding(end = 16.dp, bottom = 8.dp),
                onClick = {
                    val result = datePickerState.selectedDateMillis

                    if (result != null) {
                        onSetDeadlineClick(getSelectedDate(result))
                    } else {
                        onSetDeadlineClick("no date selected")
                    }
                    onCloseClick()
                }
            ) { Text(text = "Set deadline") }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.padding(bottom = 8.dp),
                onClick = {
                    onCloseClick()
                }
            ) { Text(text = "Close") }
        }
    ) {
        DatePicker(
            modifier = Modifier.padding(horizontal = 8.dp),
            state = datePickerState
        )
    }
}


