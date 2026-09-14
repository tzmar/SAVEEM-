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
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryEntity
import com.example.ui.components.parseColorSafe
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

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All App Data?") },
            text = {
                Text("This will permanently delete all recorded allocations, expenses, and custom goals, restoring preset categories (40%, 30%, 20%, 10%) and Botswana Pula (P).")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("Yes, Reset Everything", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add New Category") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCatName,
                        onValueChange = { newCatName = it },
                        label = { Text("Category Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCatPercentage,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() || it == '.' }) newCatPercentage = input
                        },
                        label = { Text("Percentage (%)") },
                        suffix = { Text("%") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCatDesc,
                        onValueChange = { newCatDesc = it },
                        label = { Text("Description (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
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
                    enabled = newCatName.isNotBlank() && (newCatPercentage.toDoubleOrNull() ?: 0.0) > 0.0
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        Column {
            Text(
                text = "SETTINGS & RULES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Category Allocations & Currency",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Percentage Validation Status Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isExactly100) Color(0xFF10B981).copy(alpha = 0.12f) else Color(0xFFF59E0B).copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isExactly100) Color(0xFF10B981) else Color(0xFFF59E0B)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (isExactly100) Icons.Default.Check else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isExactly100) Color(0xFF10B981) else Color(0xFFD97706),
                    modifier = Modifier.size(22.dp)
                )

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
                        color = if (isExactly100) Color(0xFF047857) else Color(0xFFB45309)
                    )
                    Text(
                        text = "The app enforces that category percentages total exactly 100%.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        feedbackMessage?.let { msg ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = msg,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
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
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = { showAddCategoryDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("add_category_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Category", fontSize = 12.sp)
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
            Button(
                onClick = { viewModel.saveCategoryPercentages() },
                enabled = isExactly100,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_categories_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isExactly100) "Save 100% Allocations" else "Must Total 100% to Save",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // Section: Currency Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Default is Botswana Pula (P). Change to any preferred currency.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf("P", "$", "€", "£", "R")
                    presets.forEach { sym ->
                        val isSelected = currentCurrency == sym
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setCurrency(sym) }
                                .testTag("currency_chip_$sym")
                        ) {
                            Text(
                                text = sym,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
                        placeholder = { Text("Custom symbol (e.g. BWP, KSh)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (customCurrencyInput.isNotBlank()) {
                                viewModel.setCurrency(customCurrencyInput.trim())
                                customCurrencyInput = ""
                            }
                        },
                        enabled = customCurrencyInput.isNotBlank(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }

        // Section: Data Export & Reset
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = { viewModel.exportData(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_csv_button"),
                    shape = RoundedCornerShape(10.dp)
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
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(10.dp)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(catColor)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        onUpdate(it, percentageText.toDoubleOrNull() ?: category.percentage, description)
                    },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
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
                    suffix = { Text("%") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(80.dp)
                )

                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete category",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
