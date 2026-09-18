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

@Composable
fun AllocationSuccessDialog(
    event: AllocationSuccessEvent,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xF20B1A22),
        modifier = Modifier
            .border(
                1.2.dp,
                Brush.verticalGradient(
                    listOf(Color(0x80FFFFFF), Color(0x3010B981), Color(0x15FFFFFF))
                ),
                RoundedCornerShape(24.dp)
            )
            .shadow(20.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x8010B981)),
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("allocation_done_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LiquidEmerald
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Done",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = LiquidMint,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Money Allocated!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFFF8FAFC)
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
                // Glass total card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x3810B981), Color(0x150D9488))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(Color(0x8034D399), Color(0x2034D399))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "TOTAL RECEIVED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LiquidMint
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatCurrency(event.allocation.totalAmount, event.currency),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFF8FAFC)
                        )
                        if (event.allocation.note.isNotBlank()) {
                            Text(
                                text = "Note: ${event.allocation.note}",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Divided Into Preset Categories:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    event.splits.forEach { split ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x25FFFFFF))
                                .border(
                                    1.dp,
                                    Color(0x33FFFFFF),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = split.categoryName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFF8FAFC)
                                )
                                Text(
                                    text = "${split.percentage.toInt()}% allocation",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text(
                                text = formatCurrency(split.amount, event.currency),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = LiquidMint
                            )
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
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
    val categoryColor = parseColorSafe(category.colorHex)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xF20B1A22),
        modifier = Modifier
            .border(
                1.2.dp,
                Brush.verticalGradient(
                    listOf(Color(0x80FFFFFF), categoryColor.copy(alpha = 0.35f), Color(0x15FFFFFF))
                ),
                RoundedCornerShape(24.dp)
            ),
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = (amount.toDoubleOrNull() ?: 0.0) > 0.0,
                colors = ButtonDefaults.buttonColors(containerColor = LiquidRose),
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
                Text("Cancel", color = Color(0xFF94A3B8))
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
                    color = Color(0xFFF8FAFC)
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
                        .background(Color(0x20FFFFFF))
                        .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Available Balance:",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = formatCurrency(category.currentBalance, currency),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFFF8FAFC)
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount Spent ($currency)") },
                    placeholder = { Text("e.g. 250") },
                    prefix = { Text(currency, fontWeight = FontWeight.Bold, color = LiquidMint) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LiquidMint,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("What did you buy? (Optional)") },
                    placeholder = { Text("e.g. Bought multimeter") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_desc_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LiquidMint,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    )
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
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
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xF20B1A22),
        modifier = Modifier
            .border(
                1.2.dp,
                Brush.verticalGradient(
                    listOf(Color(0x80FFFFFF), Color(0x3038BDF8), Color(0x15FFFFFF))
                ),
                RoundedCornerShape(24.dp)
            ),
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = title.isNotBlank() && (target.toDoubleOrNull() ?: 0.0) > 0.0,
                modifier = Modifier.testTag("save_goal_button"),
                colors = ButtonDefaults.buttonColors(containerColor = LiquidCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Goal", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
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
                    tint = LiquidCyan
                )
                Text(
                    text = "Create Financial Goal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFF8FAFC)
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
                    placeholder = { Text("e.g. Zimbabwe Business Capital") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LiquidCyan,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    )
                )

                OutlinedTextField(
                    value = target,
                    onValueChange = onTargetChange,
                    label = { Text("Target Amount ($currency)") },
                    placeholder = { Text("e.g. 10000") },
                    prefix = { Text(currency, fontWeight = FontWeight.Bold, color = LiquidCyan) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_target_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LiquidCyan,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    )
                )

                Text(
                    text = "Link to Category (tracks accumulated balance):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8)
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
                                containerColor = if (isSelected) catColor.copy(alpha = 0.22f) else Color(0x18FFFFFF)
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(
                                    if (isSelected) listOf(catColor, catColor.copy(alpha = 0.5f))
                                    else listOf(Color(0x33FFFFFF), Color(0x15FFFFFF))
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
                                    color = if (isSelected) Color(0xFFF8FAFC) else Color(0xFFE2E8F0)
                                )
                                Text(
                                    text = "${cat.percentage.toInt()}%",
                                    fontSize = 12.sp,
                                    color = if (isSelected) catColor else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
