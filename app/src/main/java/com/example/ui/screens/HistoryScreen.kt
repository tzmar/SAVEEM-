package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AllocationWithSplits
import com.example.data.model.ExpenseEntity
import com.example.ui.components.AppleSegmentedControl
import com.example.ui.components.LiquidCyan
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidMint
import com.example.ui.components.LiquidRose
import com.example.ui.components.formatCurrency
import com.example.ui.components.parseColorSafe
import com.example.ui.theme.AppleGreenLight
import com.example.ui.theme.AppleRoseLight
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.getAdaptiveAccent
import com.example.ui.viewmodel.MoneyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class HistoryItem {
    abstract val timestamp: Long

    data class Allocation(val data: AllocationWithSplits) : HistoryItem() {
        override val timestamp: Long get() = data.allocation.timestamp
    }

    data class Expense(val data: ExpenseEntity) : HistoryItem() {
        override val timestamp: Long get() = data.timestamp
    }
}

@Composable
fun HistoryScreen(
    viewModel: MoneyViewModel,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)

    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val allocations by viewModel.allocationsWithSplits.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("All") } // "All", "Allocations", "Expenses"
    var selectedTransaction by remember { mutableStateOf<HistoryItem?>(null) }

    val allHistoryItems = remember(allocations, expenses, selectedFilter) {
        val list = mutableListOf<HistoryItem>()
        if (selectedFilter == "All" || selectedFilter == "Allocations") {
            list.addAll(allocations.map { HistoryItem.Allocation(it) })
        }
        if (selectedFilter == "All" || selectedFilter == "Expenses") {
            list.addAll(expenses.map { HistoryItem.Expense(it) })
        }
        list.sortByDescending { it.timestamp }
        list
    }

    // If a transaction is tapped, show the separate detail view
    if (selectedTransaction != null) {
        TransactionDetailView(
            item = selectedTransaction!!,
            currency = currency,
            onBack = { selectedTransaction = null },
            onDeleteAllocation = { id ->
                viewModel.deleteAllocation(id)
                selectedTransaction = null
            },
            onDeleteExpense = { id ->
                viewModel.deleteExpense(id)
                selectedTransaction = null
            },
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
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
                        text = "TRANSACTION LEDGER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = if (isDark) LiquidMint else Color(0xFF0D9488)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "History",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
            }
        }

        // Apple Segmented Control Filter: All / Allocations / Expenses
        AppleSegmentedControl(
            items = listOf("All", "Allocations", "Expenses"),
            selectedItem = selectedFilter,
            onItemSelected = { selectedFilter = it },
            getItemLabel = { it }
        )

        // Clean Transactions List
        if (allHistoryItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = textColorSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "No transactions found",
                        fontSize = 14.sp,
                        color = textColorSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allHistoryItems, key = { item ->
                    when (item) {
                        is HistoryItem.Allocation -> "alloc_${item.data.allocation.id}"
                        is HistoryItem.Expense -> "exp_${item.data.id}"
                    }
                }) { item ->
                    TransactionRowCard(
                        item = item,
                        currency = currency,
                        onClick = { selectedTransaction = item }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionRowCard(
    item: HistoryItem,
    currency: String,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    when (item) {
        is HistoryItem.Allocation -> {
            val alloc = item.data.allocation
            val dateStr = remember(alloc.timestamp) { dateFormat.format(Date(alloc.timestamp)) }
            val title = if (alloc.note.isNotBlank()) alloc.note else "Income Received"
            val accent = if (isDark) LiquidMint else AppleGreenLight

            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .testTag("history_item_alloc_${alloc.id}"),
                shape = RoundedCornerShape(16.dp),
                tintColor = accent
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = if (isDark) 0.20f else 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColorPrimary
                            )
                            Text(
                                text = dateStr,
                                fontSize = 11.sp,
                                color = textColorSecondary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "+${formatCurrency(alloc.totalAmount, currency)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) LiquidMint else Color(0xFF047857)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View detail",
                            tint = textColorSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
        is HistoryItem.Expense -> {
            val exp = item.data
            val dateStr = remember(exp.timestamp) { dateFormat.format(Date(exp.timestamp)) }
            val title = if (exp.description.isNotBlank()) exp.description else "Spent from ${exp.categoryName}"
            val accent = if (isDark) LiquidRose else AppleRoseLight

            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .testTag("history_item_exp_${exp.id}"),
                shape = RoundedCornerShape(16.dp),
                tintColor = accent
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = if (isDark) 0.20f else 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColorPrimary
                            )
                            Text(
                                text = "${exp.categoryName} · $dateStr",
                                fontSize = 11.sp,
                                color = textColorSecondary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "-${formatCurrency(exp.amount, currency)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) LiquidRose else Color(0xFFBE123C)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View detail",
                            tint = textColorSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Separate full detail page for a single transaction.
 */
@Composable
fun TransactionDetailView(
    item: HistoryItem,
    currency: String,
    onBack: () -> Unit,
    onDeleteAllocation: (Long) -> Unit,
    onDeleteExpense: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val fullDateFormat = remember { SimpleDateFormat("EEEE, MMMM d, yyyy · h:mm a", Locale.getDefault()) }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = if (isDark) Color(0xFF1E2632) else Color.White,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete Transaction?", fontWeight = FontWeight.Bold, color = textColorPrimary) },
            text = { Text("Are you sure you want to permanently delete this record? Balances will be updated accordingly.", color = textColorSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        when (item) {
                            is HistoryItem.Allocation -> onDeleteAllocation(item.data.allocation.id)
                            is HistoryItem.Expense -> onDeleteExpense(item.data.id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) LiquidRose else Color(0xFFE11D48))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
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
        // Top navigation bar with Back button
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
                    .testTag("detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to History",
                    tint = textColorPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = "Transaction Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
                Text(
                    text = when (item) {
                        is HistoryItem.Allocation -> "Allocation Record"
                        is HistoryItem.Expense -> "Expense Record"
                    },
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
            }
        }

        // Hero Amount Card
        when (item) {
            is HistoryItem.Allocation -> {
                val alloc = item.data.allocation
                val dateStr = remember(alloc.timestamp) { fullDateFormat.format(Date(alloc.timestamp)) }
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    tintColor = LiquidMint
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INCOME ALLOCATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorSecondary,
                                letterSpacing = 1.1.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0x2034D399) else Color(0x200D9488))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Completed",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) LiquidMint else Color(0xFF0D9488)
                                )
                            }
                        }

                        Text(
                            text = "+${formatCurrency(alloc.totalAmount, currency)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) LiquidMint else Color(0xFF059669)
                        )

                        Text(
                            text = dateStr,
                            fontSize = 12.sp,
                            color = textColorSecondary
                        )

                        if (alloc.note.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0x18FFFFFF) else Color(0xFFF1F5F9))
                                    .border(1.dp, if (isDark) Color(0x28FFFFFF) else Color(0x20000000), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Source / Note", fontSize = 11.sp, color = textColorSecondary)
                                    Text(alloc.note, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textColorPrimary)
                                }
                            }
                        }
                    }
                }

                // Breakdown Section
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Category Splits Breakdown",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )

                        item.data.splits.forEach { split ->
                            val catColor = LiquidMint
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
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(catColor)
                                    )
                                    Column {
                                        Text(
                                            text = split.categoryName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textColorPrimary
                                        )
                                        Text(
                                            text = "${split.percentage.toInt()}% of income",
                                            fontSize = 11.sp,
                                            color = textColorSecondary
                                        )
                                    }
                                }

                                Text(
                                    text = formatCurrency(split.amount, currency),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) LiquidMint else Color(0xFF047857)
                                )
                            }
                        }
                    }
                }
            }
            is HistoryItem.Expense -> {
                val exp = item.data
                val dateStr = remember(exp.timestamp) { fullDateFormat.format(Date(exp.timestamp)) }
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    tintColor = LiquidRose
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CATEGORY EXPENSE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorSecondary,
                                letterSpacing = 1.1.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0x20F43F5E) else Color(0x20E11D48))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Deducted",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) LiquidRose else Color(0xFFE11D48)
                                )
                            }
                        }

                        Text(
                            text = "-${formatCurrency(exp.amount, currency)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) LiquidRose else Color(0xFFE11D48)
                        )

                        Text(
                            text = dateStr,
                            fontSize = 12.sp,
                            color = textColorSecondary
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0x18FFFFFF) else Color(0xFFF1F5F9))
                                .border(1.dp, if (isDark) Color(0x28FFFFFF) else Color(0x20000000), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Deducted From Category", fontSize = 11.sp, color = textColorSecondary)
                                Text(exp.categoryName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColorPrimary)
                                if (exp.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Note / Description", fontSize = 11.sp, color = textColorSecondary)
                                    Text(exp.description, fontSize = 13.sp, color = textColorPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Danger Action: Delete Record
        OutlinedButton(
            onClick = { showDeleteConfirm = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("delete_transaction_button"),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, if (isDark) LiquidRose.copy(alpha = 0.5f) else Color(0xFFE11D48)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isDark) LiquidRose else Color(0xFFE11D48)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = if (isDark) LiquidRose else Color(0xFFE11D48),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Delete Transaction",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) LiquidRose else Color(0xFFE11D48)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
