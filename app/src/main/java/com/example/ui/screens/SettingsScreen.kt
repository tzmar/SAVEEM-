package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryEntity
import com.example.ui.components.LiquidCyan
import com.example.ui.components.LiquidEmerald
import com.example.ui.components.LiquidGlassButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidMint
import com.example.ui.components.LiquidRose
import com.example.ui.components.parseColorSafe
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.viewmodel.MoneyViewModel
import kotlin.math.abs

@Composable
fun SettingsScreen(
    viewModel: MoneyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentCurrency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val editableCategories by viewModel.editableCategories.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.settingsFeedbackMessage.collectAsStateWithLifecycle()
    val currentTheme by viewModel.themeMode.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var customCurrencyInput by remember { mutableStateOf("") }

    // Dialog state for adding a category
    var newCatName by remember { mutableStateOf("") }
    var newCatPercentage by remember { mutableStateOf("") }
    var newCatDesc by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadEditableCategories()
    }

    val totalPercentage = remember(editableCategories) {
        editableCategories.sumOf { it.percentage }
    }
    val isExactly100 = remember(totalPercentage) {
        abs(totalPercentage - 100.0) <= 0.01
    }

    // Reset Confirmation Dialog (Liquid Glass)
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = Color(0xF20B1A22),
            modifier = Modifier.border(
                1.2.dp,
                Brush.verticalGradient(listOf(Color(0x80FFFFFF), Color(0x30F43F5E), Color(0x15FFFFFF))),
                RoundedCornerShape(24.dp)
            ),
            title = {
                Text("Reset All App Data?", fontWeight = FontWeight.Bold, color = Color(0xFFF8FAFC))
            },
            text = {
                Text(
                    "This will permanently delete all recorded allocations, expenses, and custom goals, restoring preset categories (40%, 30%, 20%, 10%) and Botswana Pula (P).",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LiquidRose),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("Yes, Reset Everything", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Add Category Dialog (Liquid Glass)
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            containerColor = Color(0xF20B1A22),
            modifier = Modifier.border(
                1.2.dp,
                Brush.verticalGradient(listOf(Color(0x80FFFFFF), Color(0x3038BDF8), Color(0x15FFFFFF))),
                RoundedCornerShape(24.dp)
            ),
            title = {
                Text("Add New Category", fontWeight = FontWeight.Bold, color = Color(0xFFF8FAFC))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCatName,
                        onValueChange = { newCatName = it },
                        label = { Text("Category Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LiquidCyan,
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        )
                    )
                    OutlinedTextField(
                        value = newCatPercentage,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() || it == '.' }) newCatPercentage = input
                        },
                        label = { Text("Percentage (%)") },
                        suffix = { Text("%", color = LiquidCyan) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LiquidCyan,
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        )
                    )
                    OutlinedTextField(
                        value = newCatDesc,
                        onValueChange = { newCatDesc = it },
                        label = { Text("Description (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LiquidCyan,
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = newCatPercentage.toDoubleOrNull() ?: 0.0
                        if (newCatName.isNotBlank() && p > 0.0) {
                            viewModel.addEditableCategory(
                                name = newCatName,
                                percentage = p,
                                description = newCatDesc,
                                colorHex = "#0EA5E9"
                            )
                            newCatName = ""
                            newCatPercentage = ""
                            newCatDesc = ""
                            showAddCategoryDialog = false
                        }
                    },
                    enabled = newCatName.isNotBlank() && (newCatPercentage.toDoubleOrNull() ?: 0.0) > 0.0,
                    colors = ButtonDefaults.buttonColors(containerColor = LiquidCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    val isDarkTheme = LocalIsDarkTheme.current
    val headerTextColor = if (isDarkTheme) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val bodySubTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(LiquidCyan)
                )
                Text(
                    text = "SETTINGS & RULES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = LiquidCyan
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Category Allocations & Currency",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = headerTextColor
            )
        }

        // Percentage Validation Status Banner (Liquid Glass)
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            tintColor = if (isExactly100) LiquidMint else Color(0xFFF59E0B)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            (if (isExactly100) LiquidMint else Color(0xFFF59E0B)).copy(alpha = 0.20f)
                        )
                        .border(
                            1.dp,
                            (if (isExactly100) LiquidMint else Color(0xFFF59E0B)).copy(alpha = 0.5f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExactly100) Icons.Default.Check else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isExactly100) LiquidMint else Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isExactly100) {
                            "Total Allocation: 100% (Balanced ✓)"
                        } else {
                            val diff = 100.0 - totalPercentage
                            val diffText = if (diff > 0) "Need +${String.format("%.1f", diff)}%" else "Exceeds by ${String.format("%.1f", abs(diff))}%"
                            "Total Allocation: ${String.format("%.1f", totalPercentage)}% ($diffText)"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExactly100) LiquidMint else Color(0xFFFBBF24)
                    )
                    Text(
                        text = "The app enforces that category percentages total exactly 100%.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        feedbackMessage?.let { msg ->
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                tintColor = LiquidMint
            ) {
                Text(
                    text = msg,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LiquidMint,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        // Section: Category percentage editor list
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Preset Categories (${editableCategories.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerTextColor
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x2238BDF8))
                        .border(1.dp, Color(0x6638BDF8), RoundedCornerShape(12.dp))
                        .clickable { showAddCategoryDialog = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("add_category_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = LiquidCyan, modifier = Modifier.size(16.dp))
                        Text("Add Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LiquidCyan)
                    }
                }
            }

            editableCategories.forEachIndexed { index, category ->
                EditableCategoryCard(
                    category = category,
                    canDelete = editableCategories.size > 1,
                    onUpdate = { name, percentage, desc ->
                        viewModel.updateEditableCategory(index, name, percentage, desc)
                    },
                    onDelete = { viewModel.removeEditableCategory(index) }
                )
            }

            // Save Allocations Button
            LiquidGlassButton(
                onClick = { viewModel.saveCategoryPercentages() },
                enabled = isExactly100,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_categories_button"),
                gradientColors = if (isExactly100) listOf(Color(0xFF0EA5E9), Color(0xFF10B981)) else listOf(Color(0x33FFFFFF), Color(0x1AFFFFFF))
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isExactly100) "Save 100% Allocations" else "Must Total 100% to Save",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }

        // Section: Currency Settings (Liquid Glass Card)
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            tintColor = LiquidCyan
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Currency Symbol",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerTextColor
                )
                Text(
                    text = "Default is Botswana Pula (P). Change to any preferred currency.",
                    fontSize = 12.sp,
                    color = bodySubTextColor
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf("P", "$", "€", "£", "R")
                    presets.forEach { sym ->
                        val isSelected = currentCurrency == sym
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.horizontalGradient(listOf(Color(0xFF0D9488), Color(0xFF10B981)))
                                    } else {
                                        Brush.verticalGradient(listOf(Color(0x26FFFFFF), Color(0x10FFFFFF)))
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0x33FFFFFF),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setCurrency(sym) }
                                .padding(horizontal = 16.dp, vertical = 9.dp)
                                .testTag("currency_chip_$sym")
                        ) {
                            Text(
                                text = sym,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFFE2E8F0)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customCurrencyInput,
                        onValueChange = { customCurrencyInput = it },
                        placeholder = { Text("Custom symbol (e.g. BWP, KSh)", color = Color(0xFF64748B)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LiquidCyan,
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        )
                    )
                    Button(
                        onClick = {
                            if (customCurrencyInput.isNotBlank()) {
                                viewModel.setCurrency(customCurrencyInput.trim())
                                customCurrencyInput = ""
                            }
                        },
                        enabled = customCurrencyInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = LiquidCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Section: Appearance & Themes (Liquid Glass Card)
        val isDarkThemeActive = LocalIsDarkTheme.current
        val cardTextColor = if (isDarkThemeActive) Color(0xFFF8FAFC) else Color(0xFF0F172A)
        val subTextColor = if (isDarkThemeActive) Color(0xFF94A3B8) else Color(0xFF64748B)

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            tintColor = LiquidCyan
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = LiquidCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "App Theme & Appearance",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = cardTextColor
                    )
                }

                Text(
                    text = "Select your preferred Liquid Glass aesthetic. All themes retain the specular glass highlights, glowing liquid orbs, and luminous accents.",
                    fontSize = 12.sp,
                    color = subTextColor,
                    lineHeight = 16.sp
                )

                // Theme Mode Selector Cards
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppThemeMode.values().forEach { mode ->
                        val isSelected = currentTheme == mode
                        val icon = when (mode) {
                            AppThemeMode.LIQUID_DARK -> Icons.Default.DarkMode
                            AppThemeMode.LIQUID_LIGHT -> Icons.Default.LightMode
                            AppThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) {
                                        if (isDarkThemeActive) Color(0x3306B6D4) else Color(0x250D9488)
                                    } else {
                                        if (isDarkThemeActive) Color(0x14FFFFFF) else Color(0x0A000000)
                                    }
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    brush = if (isSelected) {
                                        Brush.horizontalGradient(listOf(LiquidCyan, LiquidMint))
                                    } else {
                                        Brush.horizontalGradient(listOf(Color(0x22FFFFFF), Color(0x11FFFFFF)))
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.setThemeMode(mode) }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .testTag("theme_mode_${mode.name.lowercase()}"),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) LiquidCyan.copy(alpha = 0.25f) else Color(0x1894A3B8)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = mode.title,
                                        tint = if (isSelected) LiquidCyan else subTextColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mode.title,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) LiquidCyan else cardTextColor
                                    )
                                    Text(
                                        text = mode.subtitle,
                                        fontSize = 11.sp,
                                        color = subTextColor
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(LiquidMint),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Active",
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Data Export & Reset (Liquid Glass Card)
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Data Management",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerTextColor
                )

                OutlinedButton(
                    onClick = { viewModel.exportData(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_csv_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x40FFFFFF)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LiquidCyan
                    )
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export History (CSV)", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reset_data_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LiquidRose.copy(alpha = 0.15f),
                        contentColor = LiquidRose
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LiquidRose.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset All App Data", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun EditableCategoryCard(
    category: CategoryEntity,
    canDelete: Boolean,
    onUpdate: (String, Double, String) -> Unit,
    onDelete: () -> Unit
) {
    val catColor = parseColorSafe(category.colorHex)
    var name by remember(category.name) { mutableStateOf(category.name) }
    var percentageText by remember(category.percentage) { mutableStateOf(category.percentage.toInt().toString()) }
    var description by remember(category.description) { mutableStateOf(category.description) }

    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tintColor = catColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(catColor)
                        .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        onUpdate(it, percentageText.toDoubleOrNull() ?: category.percentage, description)
                    },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = catColor,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    )
                )

                OutlinedTextField(
                    value = percentageText,
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() }
                        percentageText = clean
                        val p = clean.toDoubleOrNull() ?: 0.0
                        onUpdate(name, p, description)
                    },
                    label = { Text("%") },
                    suffix = { Text("%", color = catColor) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(80.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = catColor,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    )
                )

                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete category",
                            tint = LiquidRose.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    onUpdate(name, percentageText.toDoubleOrNull() ?: category.percentage, it)
                },
                label = { Text("Category Description") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = catColor,
                    unfocusedBorderColor = Color(0x33FFFFFF)
                )
            )
        }
    }
}
