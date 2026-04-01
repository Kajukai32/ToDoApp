package com.arturojas32.todoapp.utils

import android.util.Patterns
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
fun getSelectedDate(result: Long): String {

    val date = Instant.ofEpochMilli(result).atZone(ZoneId.of("UTC")).toLocalDate()

//  val newCalendar = Calendar.getInstance().apply { timeInMillis = result }
//    val day = newCalendar.get(Calendar.DAY_OF_MONTH)
//    val month = newCalendar.get(Calendar.MONTH) + 1
//    val year = newCalendar.get(Calendar.YEAR)
//    val dateSelected = LocalDate.of(year, month, day)
//
//    return dateSelected.toString()
    return date.format(formatter)

}

fun getCurrentDate(): String {

    return LocalDate.now().format(formatter)
}

fun emailAndPasswordValidator(email: String, password: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 8
}