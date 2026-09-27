package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.ui.theme.LocalIsDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Liquid Glass Color Tokens
val GlassWhiteHigh = Color(0x38FFFFFF)
val GlassWhiteMid = Color(0x1FFFFFFF)
val GlassWhiteLow = Color(0x0DFFFFFF)
val GlassBorderTop = Color(0x73FFFFFF)
val GlassBorderBottom = Color(0x1AFFFFFF)

// Luminous Liquid Accents
val LiquidEmerald = Color(0xFF10B981)
val LiquidMint = Color(0xFF34D399)
val LiquidCyan = Color(0xFF06B6D4)
val LiquidTeal = Color(0xFF0D9488)
val LiquidIndigo = Color(0xFF6366F1)
val LiquidRose = Color(0xFFF43F5E)
val LiquidGold = Color(0xFFF59E0B)

/**
 * Creates a specular glass border brush mimicking light striking the bevel of thick glass.
 */
fun glassBorderBrush(
    topHighlight: Color = GlassBorderTop,
    midHighlight: Color = Color(0x33FFFFFF),
    bottomShadow: Color = GlassBorderBottom,
    tint: Color? = null
): Brush {
    val top = tint?.copy(alpha = 0.55f) ?: topHighlight
    val mid = tint?.copy(alpha = 0.20f) ?: midHighlight
    val bottom = tint?.copy(alpha = 0.08f) ?: bottomShadow
    return Brush.verticalGradient(
        colors = listOf(top, mid, bottom)
    )
}

/**
 * Ambient Liquid & Apple Background containing subtle layered glowing liquid orbs and depth.
 * Light mode uses Apple system grouped light (#F2F2F7) with maximum text readability.
 * Dark mode uses ultra-deep obsidian (#000000) with subtle ambient glow.
 */
@Composable
fun LiquidGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF070B10), // Deep obsidian
                Color(0xFF020406),
                Color(0xFF000000)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF2F2F7), // Apple System Grouped Background
                Color(0xFFEBEBF0),
                Color(0xFFE5E5EA)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Floating Ambient Liquid Orbs drawn onto Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            if (isDark) {
                // Orb 1: Controlled Cyan/Teal at top-right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x3206B6D4),
                            Color(0x140D9488),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.85f, height * 0.10f),
                        radius = width * 0.65f
                    ),
                    radius = width * 0.65f,
                    center = Offset(width * 0.85f, height * 0.10f)
                )

                // Orb 2: Emerald/Mint at center-left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x2410B981),
                            Color(0x0C059669),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.10f, height * 0.45f),
                        radius = width * 0.60f
                    ),
                    radius = width * 0.60f,
                    center = Offset(width * 0.10f, height * 0.45f)
                )
            } else {
                // Light mode: gentle ambient teal tint
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x100D9488),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.85f, height * 0.10f),
                        radius = width * 0.65f
                    ),
                    radius = width * 0.65f,
                    center = Offset(width * 0.85f, height * 0.10f)
                )
            }
        }

        // Content on top
        content()
    }
}

/**
 * Apple-style Inset Grouped / Liquid Glass Card.
 * In Light Mode: Pure white card with crisp 0.8dp outline and subtle shadow (100% visible words).
 * In Dark Mode: Elevated frosted obsidian card with specular top highlight.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    tintColor: Color? = null,
    borderWidth: Dp = 0.8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val baseFill = if (tintColor != null) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    tintColor.copy(alpha = 0.14f),
                    tintColor.copy(alpha = 0.04f),
                    Color(0x140F1620)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    tintColor.copy(alpha = 0.08f),
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFFFF)
                )
            )
        }
    } else {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF141A22), // Apple Elevated Dark card
                    Color(0xFF0F151C),
                    Color(0xFF0B1015)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFFFF), // Apple Crisp White card
                    Color(0xFFFFFFFF)
                )
            )
        }
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isDark) 5.dp else 2.dp,
                shape = shape,
                ambientColor = if (isDark) Color(0x35000000) else Color(0x08000000),
                spotColor = if (isDark) Color(0x50000000) else Color(0x10000000)
            )
            .clip(shape)
            .background(baseFill)
            .border(
                width = borderWidth,
                brush = if (isDark) {
                    glassBorderBrush(tint = tintColor)
                } else {
                    Brush.verticalGradient(
                        listOf(
                            (tintColor ?: Color(0xFF000000)).copy(alpha = if (tintColor != null) 0.25f else 0.12f),
                            Color(0x12000000)
                        )
                    )
                },
                shape = shape
            )
            .drawBehind {
                if (isDark) {
                    // Top inner specular line
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                (tintColor ?: Color.White).copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(size.width * 0.15f, 1f),
                        end = Offset(size.width * 0.85f, 1f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
    ) {
        content()
    }
}

/**
 * Apple-style Primary Action Button with fluid gradient and rounded squircle shape.
 */
@Composable
fun LiquidGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(14.dp),
    gradientColors: List<Color> = listOf(Color(0xFF0D9488), Color(0xFF10B981)),
    content: @Composable RowScope.() -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val buttonBrush = if (enabled) {
        Brush.horizontalGradient(gradientColors)
    } else {
        if (isDark) {
            Brush.horizontalGradient(listOf(Color(0x2BFFFFFF), Color(0x1AFFFFFF)))
        } else {
            Brush.horizontalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (enabled) 6.dp else 0.dp,
                shape = shape,
                ambientColor = if (enabled) gradientColors.first().copy(alpha = 0.3f) else Color.Transparent,
                spotColor = if (enabled) gradientColors.last().copy(alpha = 0.4f) else Color.Transparent
            )
            .clip(shape)
            .background(buttonBrush)
            .border(
                width = 0.8.dp,
                brush = Brush.verticalGradient(
                    colors = if (enabled) {
                        listOf(Color(0x66FFFFFF), Color(0x22FFFFFF))
                    } else {
                        listOf(Color(0x18000000), Color(0x08000000))
                    }
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * Apple & Liquid Glass Pill Badge.
 * Automatically adapts text color and container contrast for Light & Dark mode.
 */
@Composable
fun LiquidGlassPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LiquidMint
) {
    val isDark = LocalIsDarkTheme.current
    val effectiveColor = if (isDark) {
        color
    } else {
        when (color) {
            LiquidMint, LiquidEmerald -> Color(0xFF047857)  // High-contrast emerald
            LiquidCyan, LiquidTeal -> Color(0xFF0F766E)     // High-contrast teal
            LiquidRose -> Color(0xFFBE123C)                 // High-contrast rose
            LiquidGold -> Color(0xFFB45309)                 // High-contrast amber
            LiquidIndigo -> Color(0xFF4338CA)               // High-contrast indigo
            else -> color
        }
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isDark) effectiveColor.copy(alpha = 0.16f) else effectiveColor.copy(alpha = 0.10f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        effectiveColor.copy(alpha = if (isDark) 0.5f else 0.35f),
                        effectiveColor.copy(alpha = if (isDark) 0.2f else 0.15f)
                    )
                ),
                shape = CircleShape
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = effectiveColor
        )
    }
}

/**
 * Apple iOS Segmented Control with sliding pill indicator.
 */
@Composable
fun <T> AppleSegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    getItemLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val containerColor = if (isDark) Color(0xFF1E2632) else Color(0xFFE5E5EA)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(
                0.5.dp,
                if (isDark) Color(0x28FFFFFF) else Color(0x18000000),
                RoundedCornerShape(12.dp)
            )
            .padding(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                val label = getItemLabel(item)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (isSelected) {
                                if (isDark) Color(0xFF2C3545) else Color(0xFFFFFFFF)
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable { onItemSelected(item) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) {
                            if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        } else {
                            if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Floating Apple & Liquid Glass Navigation Bar.
 * Clean iOS tab bar layout with 5 tabs, responsive icon + label.
 */
@Composable
fun <T> FloatingLiquidGlassNavBar(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    getItemLabel: (T) -> String,
    getItemIcons: (T) -> Pair<ImageVector, ImageVector>,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = if (isDark) Color(0x55000000) else Color(0x14000000),
                    spotColor = if (isDark) Color(0x4006B6D4) else Color(0x200D9488)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(
                    if (isDark) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xF2121820),
                                Color(0xF50D1217)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xF8FFFFFF),
                                Color(0xF2F8FAFC)
                            )
                        )
                    }
                )
                .border(
                    width = 0.8.dp,
                    brush = if (isDark) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0x44FFFFFF),
                                Color(0x1A06B6D4),
                                Color(0x10FFFFFF)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color(0x30000000),
                                Color(0x14000000)
                            )
                        )
                    },
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item == selectedItem
                    val (selectedIcon, unselectedIcon) = getItemIcons(item)
                    val label = getItemLabel(item)

                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (isDark) LiquidMint else Color(0xFF0D9488)
                        } else {
                            if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        },
                        animationSpec = tween(durationMillis = 200),
                        label = "tab_icon_tint"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = if (isDark) LiquidMint else Color(0xFF0D9488)),
                                onClick = { onItemSelected(item) }
                            )
                            .testTag("nav_tab_${label.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                contentDescription = label,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) {
                                    if (isDark) LiquidMint else Color(0xFF0D9488)
                                } else {
                                    if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                },
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
