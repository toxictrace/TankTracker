package com.toxictrace.tanktracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TankTrackerColorScheme = darkColorScheme(
    primary = NeonOrange,
    secondary = NeonGreen,
    tertiary = SteelGray,
    background = DarkBg,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = GridOffWhite,
    onSurface = GridOffWhite,
    error = NeonRed
)

@Composable
fun TankTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TankTrackerColorScheme,
        content = content
    )
}
