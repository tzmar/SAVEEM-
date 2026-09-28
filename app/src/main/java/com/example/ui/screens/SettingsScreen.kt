package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryEntity
import com.example.ui.components.AddGoalDialog
import com.example.ui.components.LiquidCyan
import com.example.ui.components.LiquidEmerald
import com.example.ui.components.LiquidGlassButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassPill
import com.example.ui.components.LiquidGold
import com.example.ui.components.LiquidIndigo
import com.example.ui.components.LiquidMint
import com.example.ui.components.LiquidRose
import com.example.ui.components.LiquidTeal
import com.example.ui.components.formatCurrency
import com.example.ui.components.parseColorSafe
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.getAdaptiveAccent
import com.example.ui.viewmodel.MoneyViewModel
import kotlin.math.abs

enum class SettingsSubPage {
    ROOT,
    ALLOCATION_RULES,
    CATEGORIES,
    GOALS,
    CURRENCY,
    FOREX,
    BACKUP_EXPORT,
    APP_SETTINGS
}

@Composable
fun SettingsScreen(
    viewModel: MoneyViewModel,
    onNavigateToAllocate: () -> Unit,
    currentSubPage: SettingsSubPage = SettingsSubPage.ROOT,
    onSubPageChange: (SettingsSubPage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (currentSubPage) {
        SettingsSubPage.ROOT -> SettingsRootMenu(
            viewModel = viewModel,
            onSelectSubPage = onSubPageChange,
            modifier = modifier
        )
        SettingsSubPage.ALLOCATION_RULES -> AllocationRulesSubPage(
            viewModel = viewModel,
            onBack = { onSubPageChange(SettingsSubPage.ROOT) },
            modifier = modifier
        )
        SettingsSubPage.CATEGORIES -> CategoriesSubPage(
            viewModel = viewModel,
            onBack = { onSubPageChange(SettingsSubPage.ROOT) },
            modifier = modifier
        )
        SettingsSubPage.GOALS -> GoalsSubPage(
            viewModel = viewModel,
            onBack = { onSubPageChange(SettingsSubPage.ROOT) },
            modifier = modifier
        )
        SettingsSubPage.CURRENCY -> CurrencySubPage(
            viewModel = viewModel,
            onBack = { onSubPageChange(SettingsSubPage.ROOT) },
            modifier = modifier
        )
        SettingsSubPage.FOREX -> ForexScreen(
            viewModel = viewModel,
            onNavigateToAllocate = onNavigateToAllocate,
            onBack = { onSubPageChange(SettingsSubPage.ROOT) },
            modifier = modifier
        )
        SettingsSubPage.BACKUP_EXPORT -> BackupExportSubPage(
            viewModel = viewModel,
            onBack = { onSubPageChange(SettingsSubPage.ROOT) },
            modifier = modifier
        )
        SettingsSubPage.APP_SETTINGS -> AppSettingsSubPage(
            viewModel = viewModel,
            onBack = { onSubPageChange(SettingsSubPage.ROOT) },
            modifier = modifier
        )
    }
}

/**
 * Root Settings / More page showing grouped secondary features with clean iOS-style chevrons.
 */
@Composable
fun SettingsRootMenu(
    viewModel: MoneyViewModel,
    onSelectSubPage: (SettingsSubPage) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Large Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isDark) LiquidMint else Color(0xFF0D9488))
                    )
                    Text(
                        text = "CONFIGURATION & TOOLS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = if (isDark) LiquidMint else Color(0xFF0D9488)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Settings / More",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
            }
        }

        // Section 1: Money
        SettingsSectionGroup(
            title = "MONEY",
            items = listOf(
                SettingsMenuItem(
                    title = "Allocation Rules",
                    subtitle = "40% / 30% / 20% / 10% preset targets",
                    icon = Icons.Default.PieChart,
                    iconTint = LiquidMint,
                    tag = "menu_allocation_rules",
                    onClick = { onSelectSubPage(SettingsSubPage.ALLOCATION_RULES) }
                ),
                SettingsMenuItem(
                    title = "Categories",
                    subtitle = "Manage & customize budget funds",
                    icon = Icons.Default.Category,
                    iconTint = LiquidCyan,
                    tag = "menu_categories",
                    onClick = { onSelectSubPage(SettingsSubPage.CATEGORIES) }
                ),
                SettingsMenuItem(
                    title = "Savings Goals",
                    subtitle = "Targets & milestone notifications",
                    icon = Icons.Default.Flag,
                    iconTint = LiquidTeal,
                    tag = "menu_goals",
                    onClick = { onSelectSubPage(SettingsSubPage.GOALS) }
                ),
                SettingsMenuItem(
                    title = "Currency",
                    subtitle = "Default currency symbol: $currency",
                    icon = Icons.Default.AttachMoney,
                    iconTint = LiquidGold,
                    tag = "menu_currency",
                    onClick = { onSelectSubPage(SettingsSubPage.CURRENCY) }
                )
            )
        )

        // Section 2: Tools
        SettingsSectionGroup(
            title = "TOOLS",
            items = listOf(
                SettingsMenuItem(
                    title = "Forex",
                    subtitle = "USD / Pula converter & custom exchange rates",
                    icon = Icons.Default.CurrencyExchange,
                    iconTint = LiquidCyan,
                    tag = "menu_forex",
                    onClick = { onSelectSubPage(SettingsSubPage.FOREX) }
                )
            )
        )

        // Section 3: Data
        SettingsSectionGroup(
            title = "DATA",
            items = listOf(
                SettingsMenuItem(
                    title = "Backup / Export",
                    subtitle = "Export transactions & balances to JSON / CSV",
                    icon = Icons.Default.Storage,
                    iconTint = LiquidIndigo,
                    tag = "menu_backup_export",
                    onClick = { onSelectSubPage(SettingsSubPage.BACKUP_EXPORT) }
                )
            )
        )

        // Section 4: App
        SettingsSectionGroup(
            title = "APP",
            items = listOf(
                SettingsMenuItem(
                    title = "Settings",
                    subtitle = "Theme mode, goal alerts & reset data",
                    icon = Icons.Default.Settings,
                    iconTint = Color(0xFFA855F7),
                    tag = "menu_app_settings",
                    onClick = { onSelectSubPage(SettingsSubPage.APP_SETTINGS) }
                )
            )
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

data class SettingsMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color,
    val tag: String,
    val onClick: () -> Unit
)

@Composable
fun SettingsSectionGroup(
    title: String,
    items: List<SettingsMenuItem>
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp,
            color = textColorSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                items.forEachIndexed { index, item ->
                    val effectiveIconTint = getAdaptiveAccent(item.iconTint, isDark)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { item.onClick() }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .testTag(item.tag),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(effectiveIconTint.copy(alpha = if (isDark) 0.20f else 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = effectiveIconTint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary
                                )
                                Text(
                                    text = item.subtitle,
                                    fontSize = 12.sp,
                                    color = textColorSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open ${item.title}",
                            tint = textColorSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (index < items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 66.dp),
                            thickness = 0.6.dp,
                            color = if (isDark) Color(0x18FFFFFF) else Color(0x20000000)
                        )
                    }
                }
            }
        }
    }
}

/**
 * SubPage Top Bar Helper
 */
@Composable
fun SubPageTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0x22FFFFFF) else Color(0xFFF1F5F9))
                .border(1.dp, if (isDark) Color(0x30FFFFFF) else Color(0x25000000), CircleShape)
                .testTag("subpage_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to Settings",
                tint = textColorPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColorPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = textColorSecondary
            )
        }
    }
}

/**
 * 1. Allocation Rules SubPage
 */
@Composable
fun AllocationRulesSubPage(
    viewModel: MoneyViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)

    val editableCategories by viewModel.editableCategories.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.settingsFeedbackMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadEditableCategories()
    }

    val totalPercentage = remember(editableCategories) {
        editableCategories.sumOf { it.percentage }
    }
    val isExactly100 = remember(totalPercentage) {
        abs(totalPercentage - 100.0) <= 0.01
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SubPageTopBar(
            title = "Allocation Rules",
            subtitle = "Configure automatic percentage splits",
            onBack = onBack
        )

        // Feedback banner
        feedbackMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0x2010B981) else Color(0x20059669))
                    .border(1.dp, if (isDark) LiquidMint else Color(0xFF059669), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = msg,
                    fontSize = 13.sp,
                    color = if (isDark) LiquidMint else Color(0xFF059669),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Percentage Sum Pill & Instructions
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            tintColor = if (isExactly100) LiquidMint else LiquidRose
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Percentage Sum",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )

                    LiquidGlassPill(
                        text = "${String.format(java.util.Locale.US, "%.1f", totalPercentage)}%",
                        color = if (isExactly100) LiquidMint else LiquidRose
                    )
                }

                if (!isExactly100) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = LiquidRose,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Percentages must add up to exactly 100.0% to save.",
                            fontSize = 12.sp,
                            color = LiquidRose,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "Every Pula or Dollar received will be split automatically into your categories using these ratios.",
                        fontSize = 12.sp,
                        color = textColorSecondary
                    )
                }
            }
        }

        // Quick Presets
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Quick Presets",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColorSecondary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // Standard 40/30/20/10
                        if (editableCategories.size >= 4) {
                            viewModel.updateEditableCategory(0, editableCategories[0].name, 40.0, editableCategories[0].description)
                            viewModel.updateEditableCategory(1, editableCategories[1].name, 30.0, editableCategories[1].description)
                            viewModel.updateEditableCategory(2, editableCategories[2].name, 20.0, editableCategories[2].description)
                            viewModel.updateEditableCategory(3, editableCategories[3].name, 10.0, editableCategories[3].description)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0x30FFFFFF) else Color(0x30000000)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textColorPrimary)
                ) {
                    Text("40/30/20/10", fontSize = 11.sp, maxLines = 1, color = textColorPrimary)
                }
                OutlinedButton(
                    onClick = {
                        // 50/30/10/10
                        if (editableCategories.size >= 4) {
                            viewModel.updateEditableCategory(0, editableCategories[0].name, 50.0, editableCategories[0].description)
                            viewModel.updateEditableCategory(1, editableCategories[1].name, 30.0, editableCategories[1].description)
                            viewModel.updateEditableCategory(2, editableCategories[2].name, 10.0, editableCategories[2].description)
                            viewModel.updateEditableCategory(3, editableCategories[3].name, 10.0, editableCategories[3].description)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0x30FFFFFF) else Color(0x30000000)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textColorPrimary)
                ) {
                    Text("50/30/10/10", fontSize = 11.sp, maxLines = 1, color = textColorPrimary)
                }
            }
        }

        // Category Percentage Editors
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            editableCategories.forEachIndexed { index, cat ->
                val catColor = parseColorSafe(cat.colorHex)
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(catColor)
                            )
                            Column {
                                Text(
                                    text = cat.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary
                                )
                                Text(
                                    text = cat.description,
                                    fontSize = 11.sp,
                                    color = textColorSecondary
                                )
                            }
                        }

                        // Percentage Input
                        OutlinedTextField(
                            value = if (cat.percentage % 1.0 == 0.0) cat.percentage.toInt().toString() else cat.percentage.toString(),
                            onValueChange = { newVal ->
                                val parsed = newVal.toDoubleOrNull() ?: 0.0
                                viewModel.updateEditableCategory(index, cat.name, parsed, cat.description)
                            },
                            suffix = { Text("%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColorPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColorPrimary,
                                unfocusedTextColor = textColorPrimary,
                                focusedBorderColor = if (isDark) LiquidMint else Color(0xFF0D9488),
                                unfocusedBorderColor = if (isDark) Color(0x30FFFFFF) else Color(0x30000000)
                            ),
                            modifier = Modifier
                                .width(90.dp)
                                .testTag("cat_percentage_input_$index")
                        )
                    }
                }
            }
        }

        // Save Rules Button
        LiquidGlassButton(
            onClick = { viewModel.saveCategoryPercentages() },
            enabled = isExactly100,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_allocation_rules_button"),
            gradientColors = listOf(Color(0xFF0D9488), Color(0xFF10B981))
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Allocation Rules", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * 2. Categories SubPage
 */
@Composable
fun CategoriesSubPage(
    viewModel: MoneyViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryEntity?>(null) }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }

    var newCatName by remember { mutableStateOf("") }
    var newCatPercentage by remember { mutableStateOf("") }
    var newCatDesc by remember { mutableStateOf("") }
    var newCatColorHex by remember { mutableStateOf("#06B6D4") }

    val paletteColors = listOf(
        "#10B981", // Emerald
        "#06B6D4", // Cyan
        "#0D9488", // Teal
        "#6366F1", // Indigo
        "#8B5CF6", // Purple
        "#F43F5E", // Rose
        "#F59E0B", // Amber
        "#EC4899"  // Pink
    )

    // 1. Add Category Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = if (isDark) Color(0xFF1E2632) else Color.White,
            shape = RoundedCornerShape(20.dp),
            title = { Text("New Category", fontWeight = FontWeight.Bold, color = textColorPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColorPrimary,
                        unfocusedTextColor = textColorPrimary,
                        focusedBorderColor = if (isDark) LiquidMint else Color(0xFF0D9488),
                        unfocusedBorderColor = if (isDark) Color(0x30FFFFFF) else Color(0x30000000)
                    )
                    OutlinedTextField(
                        value = newCatName,
                        onValueChange = { newCatName = it },
                        label = { Text("Category Name") },
                        placeholder = { Text("e.g. Travel, Investments") },
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_category_name_input")
                    )
                    OutlinedTextField(
                        value = newCatPercentage,
                        onValueChange = { newCatPercentage = it },
                        label = { Text("Allocation Percentage (%)") },
                        placeholder = { Text("e.g. 10 (or 0)") },
                        suffix = { Text("%", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_category_percentage_input")
                    )
                    OutlinedTextField(
                        value = newCatDesc,
                        onValueChange = { newCatDesc = it },
                        label = { Text("Purpose / Description") },
                        placeholder = { Text("What is this fund for?") },
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_category_desc_input")
                    )

                    Text(
                        text = "Accent Color:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColorSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        paletteColors.forEach { hex ->
                            val color = parseColorSafe(hex)
                            val isSelected = hex.equals(newCatColorHex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (isSelected) 2.5.dp else 1.dp,
                                        if (isSelected) (if (isDark) Color.White else Color(0xFF0F172A)) else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { newCatColorHex = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            val pct = newCatPercentage.toDoubleOrNull() ?: 0.0
                            viewModel.addNewCategory(
                                name = newCatName.trim(),
                                percentage = pct,
                                description = newCatDesc.trim(),
                                colorHex = newCatColorHex
                            )
                            showAddDialog = false
                            newCatName = ""
                            newCatPercentage = ""
                            newCatDesc = ""
                            newCatColorHex = "#06B6D4"
                        }
                    },
                    enabled = newCatName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) LiquidTeal else Color(0xFF0D9488)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_add_category_button")
                ) {
                    Text("Add Category", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = textColorSecondary)
                }
            }
        )
    }

    // 2. Edit Category Dialog
    categoryToEdit?.let { cat ->
        var editName by remember(cat) { mutableStateOf(cat.name) }
        var editPercentage by remember(cat) { mutableStateOf(if (cat.percentage % 1.0 == 0.0) cat.percentage.toInt().toString() else cat.percentage.toString()) }
        var editDesc by remember(cat) { mutableStateOf(cat.description) }
        var editColorHex by remember(cat) { mutableStateOf(cat.colorHex) }

        AlertDialog(
            onDismissRequest = { categoryToEdit = null },
            containerColor = if (isDark) Color(0xFF1E2632) else Color.White,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Edit Category", fontWeight = FontWeight.Bold, color = textColorPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColorPrimary,
                        unfocusedTextColor = textColorPrimary,
                        focusedBorderColor = if (isDark) LiquidMint else Color(0xFF0D9488),
                        unfocusedBorderColor = if (isDark) Color(0x30FFFFFF) else Color(0x30000000)
                    )
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Category Name") },
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPercentage,
                        onValueChange = { editPercentage = it },
                        label = { Text("Percentage (%)") },
                        suffix = { Text("%", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Purpose / Description") },
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Accent Color:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColorSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        paletteColors.forEach { hex ->
                            val color = parseColorSafe(hex)
                            val isSelected = hex.equals(editColorHex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (isSelected) 2.5.dp else 1.dp,
                                        if (isSelected) (if (isDark) Color.White else Color(0xFF0F172A)) else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { editColorHex = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            val pct = editPercentage.toDoubleOrNull() ?: cat.percentage
                            viewModel.updateCategory(
                                cat.copy(
                                    name = editName.trim(),
                                    percentage = pct,
                                    description = editDesc.trim(),
                                    colorHex = editColorHex
                                )
                            )
                            categoryToEdit = null
                        }
                    },
                    enabled = editName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) LiquidTeal else Color(0xFF0D9488)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToEdit = null }) {
                    Text("Cancel", color = textColorSecondary)
                }
            }
        )
    }

    // 3. Delete Category Confirmation Dialog
    categoryToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            containerColor = if (isDark) Color(0xFF1E2632) else Color.White,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete ${cat.name}?", fontWeight = FontWeight.Bold, color = textColorPrimary) },
            text = {
                Text(
                    text = "Are you sure you want to delete this category? Its allocation percentage (${cat.percentage.toInt()}%) will be returned to your remaining categories.",
                    color = textColorSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(cat.id)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) LiquidRose else Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel", color = textColorSecondary)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SubPageTopBar(
            title = "Categories",
            subtitle = "Manage income distribution buckets",
            onBack = onBack
        )

        // Add Category Button
        LiquidGlassButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("add_category_button"),
            gradientColors = listOf(Color(0xFF0EA5E9), Color(0xFF0D9488))
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add New Category", fontWeight = FontWeight.Bold, color = Color.White)
        }

        // Category Cards List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            categories.forEach { cat ->
                val catColor = parseColorSafe(cat.colorHex)
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(catColor)
                            )
                            Column {
                                Text(
                                    text = cat.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColorPrimary
                                )
                                if (cat.description.isNotBlank()) {
                                    Text(
                                        text = cat.description,
                                        fontSize = 12.sp,
                                        color = textColorSecondary
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LiquidGlassPill(
                                text = "${cat.percentage.toInt()}%",
                                color = catColor
                            )

                            IconButton(
                                onClick = { categoryToEdit = cat },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Category",
                                    tint = textColorSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            if (categories.size > 1) {
                                IconButton(
                                    onClick = { categoryToDelete = cat },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Category",
                                        tint = if (isDark) LiquidRose else Color(0xFFE11D48),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * 3. Savings Goals SubPage
 */
@Composable
fun GoalsSubPage(
    viewModel: MoneyViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)

    val goals by viewModel.goalsWithProgress.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()

    val isAddGoalOpen by viewModel.isAddGoalOpen.collectAsStateWithLifecycle()
    val newGoalTitle by viewModel.newGoalTitle.collectAsStateWithLifecycle()
    val newGoalTarget by viewModel.newGoalTarget.collectAsStateWithLifecycle()
    val newGoalCatId by viewModel.newGoalCategoryId.collectAsStateWithLifecycle()

    if (isAddGoalOpen) {
        AddGoalDialog(
            currency = currency,
            title = newGoalTitle,
            target = newGoalTarget,
            selectedCategoryId = newGoalCatId,
            categories = categories,
            onTitleChange = viewModel::onGoalTitleChanged,
            onTargetChange = viewModel::onGoalTargetChanged,
            onCategorySelect = viewModel::onGoalCategorySelected,
            onConfirm = viewModel::saveNewGoal,
            onDismiss = viewModel::dismissAddGoalDialog
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SubPageTopBar(
            title = "Savings Goals",
            subtitle = "Financial milestones & progress tracking",
            onBack = onBack
        )

        // New Goal Button
        LiquidGlassButton(
            onClick = { viewModel.openAddGoalDialog() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("new_goal_button"),
            gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0D9488))
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New Savings Goal", fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (goals.isEmpty()) {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = textColorSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No active savings goals yet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColorPrimary
                    )
                    Text(
                        text = "Set targets like 'Emergency Buffer' or 'Tools Fund' to track progress.",
                        fontSize = 12.sp,
                        color = textColorSecondary
                    )
                }
            }
        } else {
            goals.forEach { goal ->
                GoalProgressCard(
                    goal = goal,
                    currency = currency,
                    onDelete = { viewModel.deleteGoal(goal.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * 4. Currency SubPage
 */
@Composable
fun CurrencySubPage(
    viewModel: MoneyViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)

    val currentCurrency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    var customCurrencyInput by remember { mutableStateOf("") }
    val feedbackMessage by viewModel.settingsFeedbackMessage.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SubPageTopBar(
            title = "Currency Settings",
            subtitle = "Set the primary monetary display symbol",
            onBack = onBack
        )

        feedbackMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0x2010B981) else Color(0x20059669))
                    .padding(12.dp)
            ) {
                Text(text = msg, color = if (isDark) LiquidMint else Color(0xFF059669), fontWeight = FontWeight.SemiBold)
            }
        }

        // Quick Currency Presets
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Preset Currencies",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )

                val currencyOptions = listOf(
                    Pair("Botswana Pula", "P"),
                    Pair("US Dollar", "$"),
                    Pair("South African Rand", "R"),
                    Pair("Euro", "€"),
                    Pair("British Pound", "£"),
                    Pair("Zimbabwean Gold", "ZiG")
                )

                currencyOptions.forEach { (name, symbol) ->
                    val isSelected = currentCurrency == symbol
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    if (isDark) Color(0x3010B981) else Color(0x200D9488)
                                } else {
                                    if (isDark) Color(0x08FFFFFF) else Color(0x06000000)
                                }
                            )
                            .border(
                                1.dp,
                                if (isSelected) {
                                    if (isDark) LiquidMint else Color(0xFF0D9488)
                                } else {
                                    Color.Transparent
                                },
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setCurrency(symbol) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$name ($symbol)",
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = textColorPrimary
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = if (isDark) LiquidMint else Color(0xFF0D9488),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Custom Symbol Input
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Custom Symbol",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customCurrencyInput,
                        onValueChange = { customCurrencyInput = it },
                        placeholder = { Text("e.g. BWP or $", color = textColorSecondary.copy(alpha = 0.6f)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColorPrimary,
                            unfocusedTextColor = textColorPrimary,
                            focusedBorderColor = if (isDark) LiquidMint else Color(0xFF0D9488),
                            unfocusedBorderColor = if (isDark) Color(0x30FFFFFF) else Color(0x30000000)
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            if (customCurrencyInput.isNotBlank()) {
                                viewModel.setCurrency(customCurrencyInput.trim())
                                customCurrencyInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) LiquidTeal else Color(0xFF0D9488)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * 5. Backup & Export SubPage
 */
@Composable
fun BackupExportSubPage(
    viewModel: MoneyViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)

    val stats by viewModel.overallStats.collectAsStateWithLifecycle()
    val allocations by viewModel.allocationsWithSplits.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SubPageTopBar(
            title = "Backup & Export",
            subtitle = "Safeguard & download your records",
            onBack = onBack
        )

        // Summary Card
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Financial Ledger Summary",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Allocations", fontSize = 12.sp, color = textColorSecondary)
                        Text("${allocations.size} entries", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                    }
                    Column {
                        Text("Expenses", fontSize = 12.sp, color = textColorSecondary)
                        Text("${expenses.size} entries", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                    }
                    Column {
                        Text("Total Received", fontSize = 12.sp, color = textColorSecondary)
                        Text(formatCurrency(stats.totalReceived, currency), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isDark) LiquidMint else Color(0xFF0D9488))
                    }
                }
            }
        }

        // Export Actions
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Export Options",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )

                // Copy CSV to Clipboard
                OutlinedButton(
                    onClick = {
                        val csv = buildString {
                            appendLine("Type,Date,Amount,Category/Note")
                            allocations.forEach {
                                appendLine("Allocation,${it.allocation.timestamp},${it.allocation.totalAmount},\"${it.allocation.note}\"")
                            }
                            expenses.forEach {
                                appendLine("Expense,${it.timestamp},${it.amount},\"${it.categoryName} - ${it.description}\"")
                            }
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("SAVEEM Export", csv))
                        Toast.makeText(context, "Export copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy CSV to Clipboard")
                }

                // Share Summary Text
                Button(
                    onClick = {
                        val summaryText = "SAVEEM Financial Summary:\nTotal Received: $currency${stats.totalReceived}\nTotal Allocated: $currency${stats.totalAllocated}\nTotal Spent: $currency${stats.totalSpent}"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, summaryText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share SAVEEM Summary"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LiquidIndigo),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Summary Report", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * 6. App Settings SubPage
 */
@Composable
fun AppSettingsSubPage(
    viewModel: MoneyViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)

    val currentTheme by viewModel.themeMode.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = if (isDark) Color(0xFF1E2632) else Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Reset All App Data?", fontWeight = FontWeight.Bold, color = textColorPrimary)
            },
            text = {
                Text(
                    "This will permanently delete all recorded allocations, expenses, and custom goals, restoring default categories (40%, 30%, 20%, 10%) and Botswana Pula (P).",
                    color = textColorSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) LiquidRose else Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("Yes, Reset Everything", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = textColorSecondary)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SubPageTopBar(
            title = "App Settings",
            subtitle = "Theme, notifications & data management",
            onBack = onBack
        )

        // Theme Selection
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Display Theme",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val themeOptions = listOf(
                        Triple(AppThemeMode.SYSTEM, "System", Icons.Default.SettingsBrightness),
                        Triple(AppThemeMode.LIQUID_DARK, "Dark", Icons.Default.DarkMode),
                        Triple(AppThemeMode.LIQUID_LIGHT, "Light", Icons.Default.LightMode)
                    )

                    themeOptions.forEach { (mode, label, icon) ->
                        val isSelected = currentTheme == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        if (isDark) Color(0x3010B981) else Color(0x200D9488)
                                    } else {
                                        if (isDark) Color(0x10FFFFFF) else Color(0xFFF1F5F9)
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) {
                                        if (isDark) LiquidMint else Color(0xFF0D9488)
                                    } else {
                                        if (isDark) Color(0x20FFFFFF) else Color(0x25000000)
                                    },
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setThemeMode(mode) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) (if (isDark) LiquidMint else Color(0xFF0D9488)) else textColorSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) textColorPrimary else textColorSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Goal Notifications Info
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Goal Milestones Notifications",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
                Text(
                    text = "The app sends local push alerts when your savings reach 50%, 75%, and 100% of target amounts. No background battery drain.",
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
            }
        }

        // Danger Zone: Reset All Data
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            tintColor = LiquidRose
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Reset All Data",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiquidRose
                )
                Text(
                    text = "Permanently wipes all allocations, transactions, and custom categories.",
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = LiquidRose),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset All Data", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
