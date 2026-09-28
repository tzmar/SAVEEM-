package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.CategorySummary
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.viewmodel.AllocationSuccessEvent
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatCurrency(amount: Double, currency: String): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }
    val formatter = if (amount % 1.0 == 0.0) {
        DecimalFormat("#,##0", symbols)
    } else {
        DecimalFormat("#,##0.00", symbols)
    }
    return "$currency${formatter.format(amount)}"
}

fun parseColorSafe(hex: String, fallback: Color = Color(0xFF10B981)): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) {
            Color((0xFF000000 or colorInt).toInt())
        } else if (clean.length == 8) {
            Color(colorInt.toInt())
        } else {
            fallback
        }
    } catch (e: Exception) {
        fallback
    }
}

fun getAccessibleTextColor(color: Color, isDark: Boolean): Color {
    if (isDark) return color
    // In light mode, ensure text color has sufficient contrast (> 4.5:1 against #FFFFFF)
    val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
    return if (luminance > 0.42) {
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

@Composable
fun AllocationSuccessDialog(
    event: AllocationSuccessEvent,
    onDismiss: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val containerBg = if (isDark) Color(0xFF141920) else Color(0xFFFFFFFF)
    val mintAccent = if (isDark) LiquidMint else Color(0xFF047857)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = containerBg,
        modifier = Modifier
            .border(
                1.dp,
                if (isDark) Color(0x33FFFFFF) else Color(0x18000000),
                RoundedCornerShape(22.dp)
            )
            .shadow(if (isDark) 16.dp else 8.dp, RoundedCornerShape(22.dp)),
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("allocation_done_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) LiquidTeal else Color(0xFF0D9488)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Done",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = mintAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Save like Tzilez! 🎉",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = textColorPrimary
                    )
                }
                Text(
                    text = "Deposit successfully allocated across your categories!",
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Received Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isDark) {
                                Brush.verticalGradient(listOf(Color(0x3510B981), Color(0x120D9488)))
                            } else {
                                Brush.verticalGradient(listOf(Color(0xFFE6FFFA), Color(0xFFCCFBF1)))
                            }
                        )
                        .border(
                            1.dp,
                            if (isDark) Color(0x4034D399) else Color(0x600D9488),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "TOTAL RECEIVED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) LiquidMint else Color(0xFF0F766E)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatCurrency(event.allocation.totalAmount, event.currency),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColorPrimary
                        )
                        if (event.allocation.note.isNotBlank()) {
                            Text(
                                text = "Note: ${event.allocation.note}",
                                fontSize = 12.sp,
                                color = textColorSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Divided Into Preset Categories:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorSecondary
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    event.splits.forEach { split ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0x20FFFFFF) else Color(0xFFF8FAFC))
                                .border(
                                    0.8.dp,
                                    if (isDark) Color(0x28FFFFFF) else Color(0x18000000),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = split.categoryName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = textColorPrimary
                                )
                                Text(
                                    text = "${split.percentage.toInt()}% allocation",
                                    fontSize = 11.sp,
                                    color = textColorSecondary
                                )
                            }
                            Text(
                                text = formatCurrency(split.amount, event.currency),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = if (isDark) LiquidMint else Color(0xFF047857)
                            )
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}

@Composable
fun RecordExpenseDialog(
    category: CategorySummary,
    currency: String,
    amount: String,
    description: String,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val categoryColor = parseColorSafe(category.colorHex)
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val containerBg = if (isDark) Color(0xFF141920) else Color(0xFFFFFFFF)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = containerBg,
        modifier = Modifier
            .border(
                1.dp,
                if (isDark) Color(0x33FFFFFF) else Color(0x18000000),
                RoundedCornerShape(22.dp)
            )
            .shadow(if (isDark) 16.dp else 8.dp, RoundedCornerShape(22.dp)),
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = (amount.toDoubleOrNull() ?: 0.0) > 0.0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) LiquidRose else Color(0xFFDC2626)
                ),
                modifier = Modifier.testTag("confirm_expense_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Record Expense",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_expense_button")
            ) {
                Text("Cancel", color = textColorSecondary)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(categoryColor)
                )
                Text(
                    text = "Spend from ${category.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textColorPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Balance badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0x20FFFFFF) else Color(0xFFF1F5F9))
                        .border(1.dp, if (isDark) Color(0x30FFFFFF) else Color(0x18000000), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Available Balance:",
                        fontSize = 12.sp,
                        color = textColorSecondary
                    )
                    Text(
                        text = formatCurrency(category.currentBalance, currency),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textColorPrimary
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount Spent ($currency)") },
                    placeholder = { Text("e.g. 250", color = textColorSecondary.copy(alpha = 0.6f)) },
                    prefix = { Text(currency, fontWeight = FontWeight.Bold, color = if (isDark) LiquidMint else Color(0xFF0F766E)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColorPrimary,
                        unfocusedTextColor = textColorPrimary,
                        focusedBorderColor = if (isDark) LiquidMint else Color(0xFF0D9488),
                        unfocusedBorderColor = if (isDark) Color(0x33FFFFFF) else Color(0x20000000)
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("What did you buy? (Optional)") },
                    placeholder = { Text("e.g. Bought multimeter", color = textColorSecondary.copy(alpha = 0.6f)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_desc_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColorPrimary,
                        unfocusedTextColor = textColorPrimary,
                        focusedBorderColor = if (isDark) LiquidMint else Color(0xFF0D9488),
                        unfocusedBorderColor = if (isDark) Color(0x33FFFFFF) else Color(0x20000000)
                    )
                )
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}

@Composable
fun AddGoalDialog(
    currency: String,
    title: String,
    target: String,
    selectedCategoryId: Long?,
    categories: List<CategoryEntity>,
    onTitleChange: (String) -> Unit,
    onTargetChange: (String) -> Unit,
    onCategorySelect: (Long?) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val containerBg = if (isDark) Color(0xFF141920) else Color(0xFFFFFFFF)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = containerBg,
        modifier = Modifier
            .border(
                1.dp,
                if (isDark) Color(0x33FFFFFF) else Color(0x18000000),
                RoundedCornerShape(22.dp)
            )
            .shadow(if (isDark) 16.dp else 8.dp, RoundedCornerShape(22.dp)),
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = title.isNotBlank() && (target.toDoubleOrNull() ?: 0.0) > 0.0,
                modifier = Modifier.testTag("save_goal_button"),
                colors = ButtonDefaults.buttonColors(containerColor = if (isDark) LiquidCyan else Color(0xFF0284C7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Goal", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColorSecondary)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = if (isDark) LiquidCyan else Color(0xFF0284C7)
                )
                Text(
                    text = "Create Financial Goal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textColorPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Goal Name") },
                    placeholder = { Text("e.g. Zimbabwe Business Capital", color = textColorSecondary.copy(alpha = 0.6f)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColorPrimary,
                        unfocusedTextColor = textColorPrimary,
                        focusedBorderColor = if (isDark) LiquidCyan else Color(0xFF0284C7),
                        unfocusedBorderColor = if (isDark) Color(0x33FFFFFF) else Color(0x20000000)
                    )
                )

                OutlinedTextField(
                    value = target,
                    onValueChange = onTargetChange,
                    label = { Text("Target Amount ($currency)") },
                    placeholder = { Text("e.g. 10000", color = textColorSecondary.copy(alpha = 0.6f)) },
                    prefix = { Text(currency, fontWeight = FontWeight.Bold, color = if (isDark) LiquidCyan else Color(0xFF0284C7)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_target_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColorPrimary,
                        unfocusedTextColor = textColorPrimary,
                        focusedBorderColor = if (isDark) LiquidCyan else Color(0xFF0284C7),
                        unfocusedBorderColor = if (isDark) Color(0x33FFFFFF) else Color(0x20000000)
                    )
                )

                Text(
                    text = "Link to Category (tracks balance automatically):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorSecondary
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategoryId == cat.id
                        val catColor = parseColorSafe(cat.colorHex)
                        OutlinedButton(
                            onClick = { onCategorySelect(cat.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) {
                                    catColor.copy(alpha = if (isDark) 0.25f else 0.12f)
                                } else {
                                    if (isDark) Color(0x18FFFFFF) else Color(0xFFF8FAFC)
                                }
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Brush.horizontalGradient(
                                    if (isSelected) listOf(catColor, catColor.copy(alpha = 0.7f))
                                    else if (isDark) listOf(Color(0x33FFFFFF), Color(0x15FFFFFF))
                                    else listOf(Color(0x18000000), Color(0x0C000000))
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cat.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) {
                                        if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                    } else {
                                        textColorSecondary
                                    }
                                )
                                Text(
                                    text = "${cat.percentage.toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) catColor else textColorSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}
