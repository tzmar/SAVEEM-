package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkTheme = staticCompositionLocalOf { true }

private val LiquidDarkColorScheme = darkColorScheme(
    primary = LiquidTealPrimary,
    onPrimary = LiquidTealOnPrimary,
    primaryContainer = LiquidTealContainer,
    onPrimaryContainer = LiquidTealOnContainer,
    secondary = LiquidCyanAccent,
    onSecondary = Color(0xFF00222D),
    secondaryContainer = Color(0xFF0C3540),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = LiquidIndigoAccent,
    background = LiquidBackground,
    surface = LiquidSurface,
    surfaceVariant = LiquidSurfaceVariant,
    onBackground = LiquidTextPrimary,
    onSurface = LiquidTextPrimary,
    onSurfaceVariant = LiquidTextSecondary,
    outline = LiquidOutline,
    error = LiquidRoseAccent
)

private val LiquidLightColorScheme = lightColorScheme(
    primary = Color(0xFF0D9488),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF115E59),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF075985),
    tertiary = Color(0xFF4F46E5),
    background = LiquidLightBackground,
    surface = LiquidLightSurface,
    surfaceVariant = LiquidLightSurfaceVariant,
    onBackground = LiquidLightTextPrimary,
    onSurface = LiquidLightTextPrimary,
    onSurfaceVariant = LiquidLightTextSecondary,
    outline = LiquidLightOutline,
    error = Color(0xFFE11D48)
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.LIQUID_DARK,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIQUID_DARK -> true
        AppThemeMode.LIQUID_LIGHT -> false
        AppThemeMode.SYSTEM -> systemDark
    }

    val colorScheme = if (isDark) LiquidDarkColorScheme else LiquidLightColorScheme

    CompositionLocalProvider(LocalIsDarkTheme provides isDark) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
