package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.data.model.CurrencyPair
import com.example.ui.components.AllocationSuccessDialog
import com.example.ui.components.AppleSegmentedControl
import com.example.ui.components.LiquidCyan
import com.example.ui.components.LiquidEmerald
import com.example.ui.components.LiquidGlassButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassPill
import com.example.ui.components.LiquidMint
import com.example.ui.components.LiquidRose
import com.example.ui.components.LiquidTeal
import com.example.ui.components.formatCurrency
import com.example.ui.components.parseColorSafe
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.viewmodel.MoneyViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ForexScreen(
    viewModel: MoneyViewModel,
    onNavigateToAllocate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val focusManager = LocalFocusManager.current

    val selectedPair by viewModel.selectedPair.collectAsStateWithLifecycle()
    val forexRate by viewModel.forexRate.collectAsStateWithLifecycle()
    val forexRateInput by viewModel.forexRateInput.collectAsStateWithLifecycle()
    val baseAmountInput by viewModel.forexBaseAmountInput.collectAsStateWithLifecycle()
    val isInverted by viewModel.isForexInverted.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.forexFeedback.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val successEvent by viewModel.allocationSuccessEvent.collectAsStateWithLifecycle()

    // Show celebration dialog if direct allocation succeeded
    successEvent?.let { event ->
        AllocationSuccessDialog(
            event = event,
            onDismiss = { viewModel.dismissAllocationDialog() }
        )
    }

    val availablePairs = viewModel.availableCurrencyPairs

    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val textColorTertiary = if (isDark) Color(0xFF64748B) else Color(0xFF64748B)

    val fromCode = if (isInverted) selectedPair.targetCode else selectedPair.baseCode
    val fromSymbol = if (isInverted) selectedPair.targetSymbol else selectedPair.baseSymbol
    val toCode = if (isInverted) selectedPair.baseCode else selectedPair.targetCode
    val toSymbol = if (isInverted) selectedPair.baseSymbol else selectedPair.targetSymbol

    val baseAmountNumeric = baseAmountInput.toDoubleOrNull() ?: 0.0
    val effectiveRate = if (isInverted) {
        if (forexRate > 0) 1.0 / forexRate else 0.0
    } else {
        forexRate
    }
    val convertedAmount = baseAmountNumeric * effectiveRate

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Apple Large Title Header
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
                            .background(if (isDark) LiquidCyan else Color(0xFF0284C7))
                    )
                    Text(
                        text = "CURRENCY & FOREX TRADE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = if (isDark) LiquidCyan else Color(0xFF0284C7)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Forex Converter",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
            }

            LiquidGlassPill(
                text = "${selectedPair.baseCode}/${selectedPair.targetCode}",
                color = LiquidCyan
            )
        }

        // Feedback toast pill if rate saved
        AnimatedVisibility(
            visible = feedbackMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            feedbackMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isDark) Color(0x3010B981) else Color(0xFFE6FFFA)
                        )
                        .border(
                            1.dp,
                            if (isDark) Color(0x6034D399) else Color(0xFF0D9488),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isDark) LiquidMint else Color(0xFF0D9488),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = msg,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F766E)
                        )
                    }
                }
            }
        }

        // Currency Pair Selector (Scrollable or Flow Chips)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "SELECT CURRENCY PAIR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = textColorTertiary
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availablePairs.forEach { pair ->
                    val isSelected = pair.id == selectedPair.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    if (isDark) Color(0x3506B6D4) else Color(0xFFE0F2FE)
                                } else {
                                    if (isDark) Color(0x18FFFFFF) else Color(0xFFFFFFFF)
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) {
                                    if (isDark) LiquidCyan else Color(0xFF0284C7)
                                } else {
                                    if (isDark) Color(0x24FFFFFF) else Color(0x18000000)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectCurrencyPair(pair) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("pair_chip_${pair.id}")
                    ) {
                        Text(
                            text = "${pair.baseCode} → ${pair.targetCode}",
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) {
                                if (isDark) LiquidCyan else Color(0xFF0284C7)
                            } else {
                                textColorSecondary
                            }
                        )
                    }
                }
            }
        }

        // 1. INTERACTIVE RATING SETTING CARD (Apple Grouped Style)
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SET EXCHANGE RATING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = textColorTertiary
                        )
                        Text(
                            text = "Set your custom rate for 1 ${selectedPair.baseCode}",
                            fontSize = 12.sp,
                            color = textColorSecondary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.resetForexRateToDefault() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Rate",
                            tint = textColorSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Interactive Rate Display & Editor
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0x20000000) else Color(0xFFF8FAFC))
                        .border(
                            1.dp,
                            if (isDark) Color(0x28FFFFFF) else Color(0x18000000),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "1 ${selectedPair.baseCode} =",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorSecondary
                        )
                    }

                    // Editable rate input
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Color(0x2606B6D4) else Color(0xFFE0F2FE))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0x6006B6D4) else Color(0xFF0284C7),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (forexRateInput.isEmpty()) {
                                Text(
                                    text = "0.0",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) LiquidCyan.copy(alpha = 0.5f) else Color(0xFF0284C7).copy(alpha = 0.5f)
                                )
                            }
                            BasicTextField(
                                value = forexRateInput,
                                onValueChange = viewModel::onForexRateInputChanged,
                                textStyle = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) LiquidCyan else Color(0xFF0284C7),
                                    textAlign = TextAlign.Center
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                    viewModel.saveForexRate()
                                }),
                                cursorBrush = SolidColor(if (isDark) LiquidCyan else Color(0xFF0284C7)),
                                modifier = Modifier
                                    .widthIn(min = 90.dp)
                                    .testTag("forex_rate_input")
                            )
                        }

                        Text(
                            text = "${selectedPair.targetSymbol} (${selectedPair.targetCode})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                    }
                }

                // Quick Adjustment Steppers (-1.0, -0.1, +0.1, +1.0)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val adjustments = listOf(-1.0, -0.1, 0.1, 1.0)
                    adjustments.forEach { delta ->
                        val label = if (delta > 0) "+$delta" else "$delta"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) Color(0x20FFFFFF) else Color(0xFFF1F5F9))
                                .border(
                                    0.8.dp,
                                    if (isDark) Color(0x28FFFFFF) else Color(0x18000000),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.adjustForexRate(delta) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary
                            )
                        }
                    }
                }

                // Save Rate Button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.saveForexRate()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("save_rate_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF06B6D4) else Color(0xFF0284C7)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save Rating (${forexRateInput} ${selectedPair.targetCode})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 2. LIVE 2-WAY CONVERSION CALCULATOR CARD (Apple Wallet Style)
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            tintColor = LiquidMint
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "CONVERT AMOUNT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = textColorTertiary
                )

                // Input Box: From Currency
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0x22000000) else Color(0xFFF8FAFC))
                        .border(
                            1.dp,
                            if (isDark) Color(0x30FFFFFF) else Color(0x18000000),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "YOU RECEIVE ($fromCode)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorTertiary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = fromSymbol,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) LiquidMint else Color(0xFF0F766E)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (baseAmountInput.isEmpty()) {
                                        Text(
                                            text = "0",
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textColorSecondary.copy(alpha = 0.5f)
                                        )
                                    }
                                    BasicTextField(
                                        value = baseAmountInput,
                                        onValueChange = viewModel::onForexBaseAmountChanged,
                                        textStyle = TextStyle(
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = textColorPrimary
                                        ),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Decimal,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                        cursorBrush = SolidColor(if (isDark) LiquidMint else Color(0xFF0D9488)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("forex_base_amount_input")
                                    )
                                }
                            }
                        }

                        LiquidGlassPill(
                            text = fromCode,
                            color = LiquidMint
                        )
                    }
                }

                // Swap direction icon button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF1E2632) else Color(0xFFE5E5EA))
                            .border(
                                1.dp,
                                if (isDark) Color(0x30FFFFFF) else Color(0x18000000),
                                CircleShape
                            )
                            .clickable { viewModel.toggleInvertForex() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap currencies",
                            tint = if (isDark) LiquidCyan else Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Output Box: Converted Target Currency
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isDark) {
                                Brush.verticalGradient(listOf(Color(0x3010B981), Color(0x140D9488)))
                            } else {
                                Brush.verticalGradient(listOf(Color(0xFFE6FFFA), Color(0xFFCCFBF1)))
                            }
                        )
                        .border(
                            1.dp,
                            if (isDark) Color(0x5034D399) else Color(0x600D9488),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CONVERTED LOCAL AMOUNT ($toCode)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) LiquidMint else Color(0xFF0F766E)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatCurrency(convertedAmount, toSymbol),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textColorPrimary
                            )
                        }

                        LiquidGlassPill(
                            text = toCode,
                            color = LiquidEmerald
                        )
                    }
                }

                // Quick Amount Presets
                Text(
                    text = "Quick amounts ($fromSymbol):",
                    fontSize = 11.sp,
                    color = textColorTertiary
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(20.0, 50.0, 100.0, 250.0, 500.0, 1000.0, 3000.0)
                    presets.forEach { preset ->
                        val isSelected = baseAmountNumeric == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        if (isDark) LiquidTeal else Color(0xFF0D9488)
                                    } else {
                                        if (isDark) Color(0x18FFFFFF) else Color(0xFFFFFFFF)
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Transparent
                                    else if (isDark) Color(0x28FFFFFF)
                                    else Color(0x18000000),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.onForexBaseAmountChanged(preset.toInt().toString())
                                    focusManager.clearFocus()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$fromSymbol${preset.toInt()}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else textColorPrimary
                            )
                        }
                    }
                }
            }
        }

        // 3. PUSH TO ALLOCATOR (PRIMARY ACTION)
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            tintColor = LiquidEmerald
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
                    Column {
                        Text(
                            text = "AUTOMATIC ALLOCATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = textColorTertiary
                        )
                        Text(
                            text = "Transfer converted money into SAVEEM Allocator",
                            fontSize = 13.sp,
                            color = textColorSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = if (isDark) LiquidMint else Color(0xFF047857),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Split Preview of this converted income
                if (convertedAmount > 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0x20000000) else Color(0xFFF8FAFC))
                            .border(
                                1.dp,
                                if (isDark) Color(0x20FFFFFF) else Color(0x18000000),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Immediate split of ${formatCurrency(convertedAmount, toSymbol)}:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColorSecondary
                        )

                        categories.forEach { cat ->
                            val catColor = parseColorSafe(cat.colorHex)
                            val catSplit = convertedAmount * (cat.percentage / 100.0)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(catColor)
                                    )
                                    Text(
                                        text = cat.name,
                                        fontSize = 12.sp,
                                        color = textColorPrimary
                                    )
                                }
                                Text(
                                    text = formatCurrency(catSplit, toSymbol),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) LiquidMint else Color(0xFF047857)
                                )
                            }
                        }
                    }
                }

                // Large Primary Action: Directly allocate money
                LiquidGlassButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.allocateForexDirectly()
                    },
                    enabled = convertedAmount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("allocate_forex_directly_button"),
                    gradientColors = listOf(Color(0xFF0D9488), Color(0xFF10B981))
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (convertedAmount > 0) {
                            "Allocate ${formatCurrency(convertedAmount, toSymbol)} Directly Now"
                        } else {
                            "Enter amount to allocate"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Secondary Action: Customize in Allocator Screen
                if (convertedAmount > 0) {
                    OutlinedButton(
                        onClick = {
                            viewModel.transferForexToAllocator(onSuccess = onNavigateToAllocate)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("transfer_forex_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isDark) Color(0x4006B6D4) else Color(0x400284C7)
                        )
                    ) {
                        Text(
                            text = "Customize in Allocator Tab →",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) LiquidCyan else Color(0xFF0284C7)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
