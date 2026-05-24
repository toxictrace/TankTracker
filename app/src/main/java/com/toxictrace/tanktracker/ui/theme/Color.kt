package com.toxictrace.tanktracker.ui.theme

import androidx.compose.ui.graphics.Color

val DarkBg = Color(0xFF0D0F11)
val DarkSurface = Color(0xFF14181C)
val DarkSurfaceLighter = Color(0xFF1D2228)
val DarkCardBorder = Color(0xFF22272C)

val NeonOrange = Color(0xFFF59E0B)
val NeonOrangeLight = Color(0xFFFFB020)
val NeonGreen = Color(0xFF10B981)
val NeonRed = Color(0xFFEF4444)
val SteelGray = Color(0xFF7F8E9C)
val GridOffWhite = Color(0xFFE2EDF8)

val WN8Red = Color(0xFFFE4C4C)
val WN8Orange = Color(0xFFF98D24)
val WN8Yellow = Color(0xFFF0D655)
val WN8Olive = Color(0xFF84A65C)
val WN8Green = Color(0xFF44D944)
val WN8Turquoise = Color(0xFF02C9C3)
val WN8Purple = Color(0xFFA636D0)

fun getWn8ComposeColor(wn8: Int): Color = when {
    wn8 >= 2900 -> WN8Purple
    wn8 >= 2450 -> WN8Turquoise
    wn8 >= 2000 -> WN8Green
    wn8 >= 1600 -> WN8Olive
    wn8 >= 1200 -> WN8Yellow
    wn8 >= 900  -> WN8Orange
    else        -> WN8Red
}
