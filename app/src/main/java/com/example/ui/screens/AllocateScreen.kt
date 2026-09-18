package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AllocationSuccessDialog
import com.example.ui.components.LiquidCyan
import com.example.ui.components.LiquidEmerald
import com.example.ui.components.LiquidGlassButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassPill
import com.example.ui.components.LiquidMint
import com.example.ui.components.LiquidTeal
import com.example.ui.components.formatCurrency
import com.example.ui.components.parseColorSafe
import com.example.ui.viewmodel.MoneyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllocateScreen(
    viewModel: MoneyViewModel,
    modifier: Modifier = Modifier
) {
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val amountInput by viewModel.amountInput.collectAsStateWithLifecycle()
    val noteInput by viewModel.noteInput.collectAsStateWithLifecycle()
    val splitPreviews by viewModel.splitPreviews.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val successEvent by viewModel.allocationSuccessEvent.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var showNoteField by remember { mutableStateOf(false) }

    val numericAmount = amountInput.toDoubleOrNull() ?: 0.0
    val isReadyToAllocate = numericAmount > 0.0

    // Show celebration dialog if allocation succeeded
    successEvent?.let { event ->
        AllocationSuccessDialog(
            event = event,
            onDismiss = { viewModel.dismissAllocationDialog() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App header badge
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
                            .background(LiquidMint)
                    )
                    Text(
                        text = "LIQUID ALLOCATOR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = LiquidMint
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Split Every Dollar Intentionally",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            LiquidGlassPill(
                text = "Currency: $currency",
                color = LiquidCyan
            )
        }

        // Core Hero Card: "How much did you receive?"
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How much did you receive?",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Frosted Liquid Glass Amount Input Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x35000000),
                                    Color(0x20000000)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.verticalGradient(
                                if (isReadyToAllocate) {
                                    listOf(LiquidMint, LiquidCyan.copy(alpha = 0.5f), Color(0x33FFFFFF))
                                } else {
                                    listOf(Color(0x4DFFFFFF), Color(0x1AFFFFFF))
                                }
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                        .testTag("amount_input_container")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currency,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LiquidMint
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (amountInput.isEmpty()) {
                                Text(
                                    text = "0",
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0x4D94A3B8)
                                )
                            }
                            BasicTextField(
                                value = amountInput,
                                onValueChange = viewModel::onAmountChanged,
                                textStyle = TextStyle(
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF8FAFC)
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                cursorBrush = SolidColor(LiquidMint),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("amount_input_field")
                            )
                        }

                        if (amountInput.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onAmountChanged("") },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear amount",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Small-income friendly quick-tap presets
                Text(
                    text = "Quick amounts:",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(20.0, 50.0, 100.0, 500.0, 1000.0, 3000.0)
                    presets.forEach { preset ->
                        val isSelected = numericAmount == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0D9488), Color(0xFF10B981))
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(Color(0x2EFFFFFF), Color(0x14FFFFFF))
                                        )
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    brush = Brush.verticalGradient(
                                        if (isSelected) listOf(Color(0xCCFFFFFF), Color(0x66FFFFFF))
                                        else listOf(Color(0x33FFFFFF), Color(0x14FFFFFF))
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    viewModel.onQuickAmountSelected(preset)
                                    focusManager.clearFocus()
                                }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                                .testTag("quick_amount_${preset.toInt()}")
                        ) {
                            Text(
                                text = "$currency${preset.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFFE2E8F0)
                            )
                        }
                    }
                }

                // Optional note toggle
                Spacer(modifier = Modifier.height(14.dp))
                if (!showNoteField && noteInput.isEmpty()) {
                    Text(
                        text = "+ Add optional note (e.g. Freelance project)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = LiquidCyan,
                        modifier = Modifier
                            .clickable { showNoteField = true }
                            .padding(vertical = 4.dp)
                    )
                }

                AnimatedVisibility(
                    visible = showNoteField || noteInput.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = viewModel::onNoteChanged,
                        placeholder = { Text("Income source / note (optional)", fontSize = 13.sp, color = Color(0xFF64748B)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .testTag("income_note_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LiquidCyan,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedTextColor = Color(0xFFF8FAFC),
                            unfocusedTextColor = Color(0xFFF8FAFC)
                        )
                    )
                }
            }
        }

        // Live Preset Allocation Split Breakdown
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(LiquidTeal.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = LiquidMint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Automatic Category Split",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFF8FAFC)
                        )
                    }

                    LiquidGlassPill(
                        text = "100% Total",
                        color = LiquidMint
                    )
                }

                Text(
                    text = if (isReadyToAllocate) {
                        "Here is where your $currency${amountInput} will go immediately:"
                    } else {
                        "Your preset percentage allocation rules:"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                // List of split rows
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isReadyToAllocate && splitPreviews.isNotEmpty()) {
                        splitPreviews.forEach { preview ->
                            val catColor = parseColorSafe(preview.colorHex)
                            CategorySplitRow(
                                name = preview.categoryName,
                                percentage = preview.percentage,
                                amountText = formatCurrency(preview.amount, currency),
                                color = catColor,
                                isHighlighted = true
                            )
                        }
                    } else {
                        categories.forEach { cat ->
                            val catColor = parseColorSafe(cat.colorHex)
                            val exampleSplit = 100.0 * (cat.percentage / 100.0)
                            CategorySplitRow(
                                name = cat.name,
                                percentage = cat.percentage,
                                amountText = "P${exampleSplit.toInt()} per P100",
                                color = catColor,
                                isHighlighted = false
                            )
                        }
                    }
                }
            }
        }

        // Primary Action: "Allocate Money"
        LiquidGlassButton(
            onClick = {
                focusManager.clearFocus()
                viewModel.allocateMoney()
            },
            enabled = isReadyToAllocate,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .testTag("allocate_money_button"),
            gradientColors = listOf(Color(0xFF0EA5E9), Color(0xFF10B981), Color(0xFF34D399))
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (isReadyToAllocate) "Allocate $currency$amountInput Now" else "Allocate Money",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CategorySplitRow(
    name: String,
    percentage: Double,
    amountText: String,
    color: Color,
    isHighlighted: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isHighlighted) {
                    Brush.horizontalGradient(
                        listOf(color.copy(alpha = 0.20f), color.copy(alpha = 0.05f))
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(Color(0x22FFFFFF), Color(0x0EFFFFFF))
                    )
                }
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    if (isHighlighted) {
                        listOf(color.copy(alpha = 0.6f), color.copy(alpha = 0.15f))
                    } else {
                        listOf(Color(0x2BFFFFFF), Color(0x10FFFFFF))
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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
                        .background(color)
                        .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                )
                Column {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
                    )
                    Text(
                        text = "${percentage.toInt()}% of income",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Text(
                text = amountText,
                fontSize = if (isHighlighted) 17.sp else 13.sp,
                fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (isHighlighted) color else Color(0xFF94A3B8)
            )
        }
    }
}
