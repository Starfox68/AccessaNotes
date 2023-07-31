package com.shaphr.accessanotes.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    primaryContainer = PinkLight

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

private val ColourBlindScheme = lightColorScheme(
    primary = Indigo,
    secondary = Yellow,
    tertiary = DarkYellow,
    background = Color(0xFFFFFFFF)
)

@Composable
fun AccessaNotesTheme(
    isLargeFont: Boolean,
    isColourBlind: Boolean,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isColourBlind -> ColourBlindScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val showDialog = remember { mutableStateOf(true) }
    if (showDialog.value) {
        MyAppDialog(
            onDismiss = {
                showDialog.value = false
            }
        )
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = if (isLargeFont) largeTypography else defaultTypography,
        content = content
    )
}

@Composable
fun MyAppDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Risk Acknowledgment") },
        text = { Text("By using our note transcription app powered by AI, you acknowledge the risk of potential addiction. This app is not a substitute for attentive listening in class or professional note-taking. We hold no responsibility for any emotional, physical, or other damages resulting from its usage. Users must exercise caution and discretion while using the app. By proceeding, you agree to these terms and conditions.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}