package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// SAVEEM iOS & Liquid Glass Color Palette

// Primary Teal / Mint Brand
val LiquidTealPrimary = Color(0xFF0D9488) // Deep refined teal
val LiquidTealOnPrimary = Color(0xFFFFFFFF)
val LiquidTealContainer = Color(0xFF134E4A)
val LiquidTealOnContainer = Color(0xFF99F6E4)

// Vibrant Accents (Dark Mode)
val LiquidCyanAccent = Color(0xFF06B6D4)
val LiquidEmeraldAccent = Color(0xFF10B981)
val LiquidMintAccent = Color(0xFF34D399)
val LiquidIndigoAccent = Color(0xFF6366F1)
val LiquidAmberAccent = Color(0xFFF59E0B)
val LiquidRoseAccent = Color(0xFFF43F5E)

// High-Contrast Light Mode Accents (High legibility on white/light backgrounds)
val AppleTealLight = Color(0xFF0F766E)      // Crisp high-contrast teal
val AppleGreenLight = Color(0xFF047857)     // Deep emerald green for positive numbers
val AppleBlueLight = Color(0xFF0284C7)      // Crisp sapphire/iOS blue
val AppleAmberLight = Color(0xFFB45309)     // Rich amber/gold
val AppleRoseLight = Color(0xFFBE123C)      // Crisp deep rose/red for expenses
val AppleIndigoLight = Color(0xFF4338CA)    // Deep indigo

// iOS System Dark Palette (Obsidian + Frosted Dark Glass)
val LiquidBackground = Color(0xFF000000)          // iOS system background dark
val LiquidSurface = Color(0xFF141920)             // iOS secondary grouped background dark
val LiquidSurfaceVariant = Color(0xFF1E2633)      // Elevated surface dark
val LiquidTextPrimary = Color(0xFFF8FAFC)         // Crisp white text
val LiquidTextSecondary = Color(0xFF94A3B8)       // Legible slate-400
val LiquidOutline = Color(0x30FFFFFF)             // Hairline glass border

// iOS System Light Palette (High-Contrast, Pure Apple Style)
val LiquidLightBackground = Color(0xFFF2F2F7)     // iOS System Grouped Background (Light)
val LiquidLightSurface = Color(0xFFFFFFFF)        // Apple Card Surface (Crisp White)
val LiquidLightSurfaceVariant = Color(0xFFE5E5EA) // Inset group border / card fill
val LiquidLightTextPrimary = Color(0xFF0A0F1D)    // Ultra-crisp near-black text (100% visible)
val LiquidLightTextSecondary = Color(0xFF334155)  // Deep slate-700 secondary label (high contrast)
val LiquidLightTextTertiary = Color(0xFF475569)   // Slate-600 captions (crisp contrast)
val LiquidLightOutline = Color(0xFF94A3B8)        // Hairline & border outline (high visibility)

/**
 * Returns an accessible, vibrant, high-contrast version of any accent color when in light mode.
 * Preserves the exact iOS aesthetic while guaranteeing 100% text/icon visibility.
 */
fun getAdaptiveAccent(color: Color, isDark: Boolean): Color {
    if (isDark) return color
    return when (color) {
        LiquidMintAccent, Color(0xFF34D399) -> AppleGreenLight
        LiquidEmeraldAccent, Color(0xFF10B981) -> AppleGreenLight
        LiquidCyanAccent, Color(0xFF06B6D4) -> AppleBlueLight
        LiquidTealPrimary, Color(0xFF0D9488) -> AppleTealLight
        LiquidRoseAccent, Color(0xFFF43F5E), Color(0xFFE11D48) -> AppleRoseLight
        LiquidAmberAccent, Color(0xFFF59E0B) -> AppleAmberLight
        LiquidIndigoAccent, Color(0xFF6366F1) -> AppleIndigoLight
        Color(0xFFA855F7) -> Color(0xFF7E22CE)
        else -> {
            val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
            if (luminance > 0.35) {
                Color(
                    red = (color.red * 0.55f).coerceIn(0f, 1f),
                    green = (color.green * 0.55f).coerceIn(0f, 1f),
                    blue = (color.blue * 0.55f).coerceIn(0f, 1f),
                    alpha = 1f
                )
            } else {
                color
            }
        }
    }
}
