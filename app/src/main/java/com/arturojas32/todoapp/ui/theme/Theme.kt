package com.arturojas32.todoapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.arturojas32.todoapp.ui.theme.OnPrimaryContainerTodoAppD
import com.arturojas32.todoapp.ui.theme.SurfaceTodoAppD

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTodoAppD,
    onPrimary = OnPrimaryTodoAppD,
    surface = SurfaceTodoAppD,
    onSurface = OnSurfaceTodoAppD,
    surfaceVariant = OnSurfaceVariantTodoAppD,
    primaryContainer = PrimaryContainerTodoAppD,
    onPrimaryContainer = OnPrimaryContainerTodoAppD,
    tertiary = TertiaryTodoAppD,
    onTertiary = OnTertiaryTodoAppD,
    tertiaryContainer = TertiaryContainerTodoAppD,
    onTertiaryContainer = OnTertiaryContainerTodoAppD,
    secondary = SecondaryTodoAppD,
    onSecondary = OnSecondaryTodoAppD,
    secondaryContainer = SecondaryTodoAppD,
    onSecondaryContainer = OnSecondaryContainerTodoAppD
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTodoAppL,
    onPrimary = OnPrimaryTodoAppL,
    surface = SurfaceTodoAppL,
    onSurface = OnSurfaceTodoAppL,
    surfaceVariant = OnSurfaceVariantTodoAppL,
    primaryContainer = PrimaryContainerTodoAppL,
    onPrimaryContainer = OnPrimaryContainerTodoAppL,
    tertiary = TertiaryTodoAppL,
    onTertiary = OnTertiaryTodoAppL,
    tertiaryContainer = TertiaryContainerTodoAppL,
    onTertiaryContainer = OnTertiaryContainerTodoAppL,
    secondary = SecondaryTodoAppL,
    onSecondary = OnSecondaryTodoAppL,
    secondaryContainer = SecondaryTodoAppL,
    onSecondaryContainer = OnSecondaryContainerTodoAppL
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ToDoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}