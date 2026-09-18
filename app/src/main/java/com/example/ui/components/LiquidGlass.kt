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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.ui.theme.LocalIsDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
    val top = tint?.copy(alpha = 0.65f) ?: topHighlight
    val mid = tint?.copy(alpha = 0.25f) ?: midHighlight
    val bottom = tint?.copy(alpha = 0.12f) ?: bottomShadow
    return Brush.verticalGradient(
        colors = listOf(top, mid, bottom)
    )
}

/**
 * Ambient Liquid Background containing layered glowing liquid orbs and dark obsidian depth.
 * Frosted translucent glass components placed over this background will naturally catch the liquid glow.
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
                Color(0xFF071317), // Deep obsidian teal
                Color(0xFF050E12),
                Color(0xFF03080A)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF0FDF4), // Luminous ice/mint white
                Color(0xFFE2E8F0),
                Color(0xFFCBD5E1)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Floating Ambient Liquid Orbs drawn onto Canvas with rich radial blur
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            if (isDark) {
                // Orb 1: Glowing Cyan/Teal at top-right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x5506B6D4),
                            Color(0x300D9488),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.85f, height * 0.12f),
                        radius = width * 0.7f
                    ),
                    radius = width * 0.7f,
                    center = Offset(width * 0.85f, height * 0.12f)
                )

                // Orb 2: Emerald/Mint at center-left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x4510B981),
                            Color(0x20059669),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.10f, height * 0.45f),
                        radius = width * 0.65f
                    ),
                    radius = width * 0.65f,
                    center = Offset(width * 0.10f, height * 0.45f)
                )

                // Orb 3: Luminous Indigo/Electric Blue at bottom-right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x356366F1),
                            Color(0x180284C7),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.80f, height * 0.85f),
                        radius = width * 0.75f
                    ),
                    radius = width * 0.75f,
                    center = Offset(width * 0.80f, height * 0.85f)
                )
            } else {
                // Light mode liquid orbs: soft cyan, emerald, and indigo glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x4038BDF8),
                            Color(0x202DD4BF),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.85f, height * 0.12f),
                        radius = width * 0.7f
                    ),
                    radius = width * 0.7f,
                    center = Offset(width * 0.85f, height * 0.12f)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x3010B981),
                            Color(0x1534D399),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.10f, height * 0.45f),
                        radius = width * 0.65f
                    ),
                    radius = width * 0.65f,
                    center = Offset(width * 0.10f, height * 0.45f)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x25818CF8),
                            Color(0x1038BDF8),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.80f, height * 0.85f),
                        radius = width * 0.75f
                    ),
                    radius = width * 0.75f,
                    center = Offset(width * 0.80f, height * 0.85f)
                )
            }
        }

        // Content on top
        content()
    }
}

/**
 * Liquid Glass Card with specular beveled border, frosted gradient fill, and subtle gloss highlight.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    tintColor: Color? = null,
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val baseFill = if (tintColor != null) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    tintColor.copy(alpha = 0.18f),
                    tintColor.copy(alpha = 0.06f),
                    Color(0x140F172A)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    tintColor.copy(alpha = 0.15f),
                    Color(0xCCFFFFFF),
                    Color(0xF0FFFFFF)
                )
            )
        }
    } else {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0x2E2A4350), // Frosted translucent dark glass
                    Color(0x18182830),
                    Color(0x120C171C)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xF2FFFFFF), // Luminous frosted light glass
                    Color(0xE6F8FAFC),
                    Color(0xDBF1F5F9)
                )
            )
        }
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = tintColor?.copy(alpha = 0.25f) ?: Color(0x40000000),
                spotColor = tintColor?.copy(alpha = 0.45f) ?: Color(0x60000000)
            )
            .clip(shape)
            .background(baseFill)
            .border(
                width = borderWidth,
                brush = glassBorderBrush(tint = tintColor),
                shape = shape
            )
            .drawBehind {
                // Top inner glass reflection line
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            (tintColor ?: Color.White).copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(size.width * 0.15f, 1f),
                    end = Offset(size.width * 0.85f, 1f),
                    strokeWidth = 1.5f
                )
            }
    ) {
        content()
    }
}

/**
 * Liquid Glass Primary Button with luminous fluid gradient, specular highlight, and sleek border.
 */
@Composable
fun LiquidGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(18.dp),
    gradientColors: List<Color> = listOf(Color(0xFF0EA5E9), Color(0xFF10B981)),
    content: @Composable RowScope.() -> Unit
) {
    val buttonBrush = if (enabled) {
        Brush.horizontalGradient(gradientColors)
    } else {
        Brush.horizontalGradient(listOf(Color(0x2BFFFFFF), Color(0x1AFFFFFF)))
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = shape,
                ambientColor = if (enabled) gradientColors.first().copy(alpha = 0.4f) else Color.Transparent,
                spotColor = if (enabled) gradientColors.last().copy(alpha = 0.6f) else Color.Transparent
            )
            .clip(shape)
            .background(buttonBrush)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = if (enabled) {
                        listOf(Color(0x80FFFFFF), Color(0x33FFFFFF), Color(0x10FFFFFF))
                    } else {
                        listOf(Color(0x22FFFFFF), Color(0x0DFFFFFF))
                    }
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                onClick = onClick
            )
            .drawBehind {
                if (enabled) {
                    // Top gloss sheen arc
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(size.width * 0.2f, 1f),
                        end = Offset(size.width * 0.8f, 1f),
                        strokeWidth = 2f
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * Liquid Glass Pill Badge
 */
@Composable
fun LiquidGlassPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LiquidMint
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.6f), color.copy(alpha = 0.2f))
                ),
                shape = CircleShape
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Floating Liquid Glass Navigation Bar with pill items, glowing active indicators,
 * and beveled translucent glass backdrop.
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
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Floating pill glass container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = if (isDark) Color(0x55000000) else Color(0x20000000),
                    spotColor = if (isDark) Color(0x8006B6D4) else Color(0x300D9488)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    if (isDark) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xE0102028), // Frosted dark glass
                                Color(0xDE0A161C),
                                Color(0xF0060E12)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xF5FFFFFF), // Frosted light glass
                                Color(0xECF8FAFC),
                                Color(0xE0E2E8F0)
                            )
                        )
                    }
                )
                .border(
                    width = 1.2.dp,
                    brush = if (isDark) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x80FFFFFF),
                                Color(0x3306B6D4),
                                Color(0x1AFFFFFF)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x90FFFFFF),
                                Color(0x400D9488),
                                Color(0x200D9488)
                            )
                        )
                    },
                    shape = RoundedCornerShape(32.dp)
                )
                .drawBehind {
                    // Top glass specular reflection
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(Color.Transparent, Color(0x60FFFFFF), Color(0x8038BDF8), Color.Transparent)
                        ),
                        start = Offset(size.width * 0.15f, 1f),
                        end = Offset(size.width * 0.85f, 1f),
                        strokeWidth = 1.5f
                    )
                }
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item == selectedItem
                    val (selectedIcon, unselectedIcon) = getItemIcons(item)
                    val label = getItemLabel(item)

                    val activeGlowAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0f,
                        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
                        label = "active_glow"
                    )

                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (isDark) LiquidMint else Color(0xFF0D9488)
                        } else {
                            if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        },
                        animationSpec = tween(durationMillis = 200),
                        label = "icon_tint"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = if (isDark) LiquidMint else Color(0xFF0D9488)),
                                onClick = { onItemSelected(item) }
                            )
                            .testTag("nav_tab_${label.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Liquid glowing active pill
                        if (activeGlowAlpha > 0.01f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            if (isDark) {
                                                listOf(
                                                    Color(0xFF0D9488).copy(alpha = 0.35f * activeGlowAlpha),
                                                    Color(0xFF10B981).copy(alpha = 0.25f * activeGlowAlpha)
                                                )
                                            } else {
                                                listOf(
                                                    Color(0xFFCCFBF1).copy(alpha = 0.8f * activeGlowAlpha),
                                                    Color(0xFFA7F3D0).copy(alpha = 0.6f * activeGlowAlpha)
                                                )
                                            }
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            if (isDark) {
                                                listOf(
                                                    LiquidMint.copy(alpha = 0.6f * activeGlowAlpha),
                                                    Color.Transparent
                                                )
                                            } else {
                                                listOf(
                                                    Color(0xFF0D9488).copy(alpha = 0.5f * activeGlowAlpha),
                                                    Color.Transparent
                                                )
                                            }
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                            )
                        }

                        // Icon and Label
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                contentDescription = label,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
