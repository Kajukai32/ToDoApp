package com.arturojas32.todoapp.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MyAnimatedConfirmButton(
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    onConfirmClick: () -> Unit, text: String = ""
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedButtonBorderColor by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary,
        targetValue = MaterialTheme.colorScheme.primaryContainer,
        animationSpec = infiniteRepeatable(
            animation = tween(2000), repeatMode = RepeatMode.Reverse
        )
    )
    val animatedButtonBorderWidthSize: Dp by infiniteTransition.animateValue(
        initialValue = 0.8.dp,
        targetValue = 3.0.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500), repeatMode = RepeatMode.Reverse
        )
    )
    Button(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            width = if (!isLoading) {
                animatedButtonBorderWidthSize
            } else {
                1.dp
            },
            color = if (!isLoading) {
                animatedButtonBorderColor
            } else {
                MaterialTheme.colorScheme.primary
            }
        ),
        enabled = isButtonEnabled,
        onClick = { onConfirmClick() }, shape = RoundedCornerShape(16)
    ) {
        Text(
            text = text,
            color = animatedButtonBorderColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}