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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AllocationWithSplits
import com.example.data.model.ExpenseEntity
import com.example.ui.components.LiquidCyan
import com.example.ui.components.LiquidEmerald
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidMint
import com.example.ui.components.LiquidRose
import com.example.ui.components.formatCurrency
import com.example.ui.components.parseColorSafe
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
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val allocations by viewModel.allocationsWithSplits.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val summaries by viewModel.categorySummaries.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("All") } // "All", "Allocations", "Expenses"

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

    val totalAllocatedOverall = remember(summaries) {
        summaries.sumOf { it.totalAllocated }
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
                            .background(LiquidMint)
                    )
                    Text(
                        text = "TRANSACTION LEDGER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = LiquidMint
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Money Allocation History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC)
                )
            }
        }

        // Summary Card: "Total Money Allocated to Each Category Over Time"
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Total Allocated Over Time",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC)
                )

                if (totalAllocatedOverall <= 0.0) {
                    Text(
                        text = "No allocations yet. When you receive money, your cumulative category distribution will show here.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                } else {
                    summaries.forEach { cat ->
                        val catColor = parseColorSafe(cat.colorHex)
                        val proportion = (cat.totalAllocated / totalAllocatedOverall).toFloat().coerceIn(0f, 1f)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                    )
                                    Text(
                                        text = cat.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFF8FAFC)
                                    )
                                }
                                Text(
                                    text = formatCurrency(cat.totalAllocated, currency),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = catColor
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0x28FFFFFF))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(proportion)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(catColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Frosted Glass Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Allocations", "Expenses").forEach { filter ->
                val isSelected = selectedFilter == filter
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
                                    listOf(Color(0x26FFFFFF), Color(0x10FFFFFF))
                                )
                            }
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                if (isSelected) listOf(Color(0xCCFFFFFF), Color(0x40FFFFFF))
                                else listOf(Color(0x30FFFFFF), Color(0x10FFFFFF))
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filter,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                    )
                }
            }
        }

        // Transactions List
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
                        tint = Color(0x6694A3B8),
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "No transactions recorded yet",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allHistoryItems, key = { item ->
                    when (item) {
                        is HistoryItem.Allocation -> "alloc_${item.data.allocation.id}"
                        is HistoryItem.Expense -> "exp_${item.data.id}"
                    }
                }) { item ->
                    when (item) {
                        is HistoryItem.Allocation -> {
                            AllocationHistoryCard(
                                item = item.data,
                                currency = currency,
                                onDelete = { viewModel.deleteAllocation(item.data.allocation.id) }
                            )
                        }
                        is HistoryItem.Expense -> {
                            ExpenseHistoryCard(
                                item = item.data,
                                currency = currency,
                                onDelete = { viewModel.deleteExpense(item.data.id) }
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun AllocationHistoryCard(
    item: AllocationWithSplits,
    currency: String,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("d MMMM yyyy, h:mm a", Locale.getDefault()) }
    val dateString = remember(item.allocation.timestamp) {
        dateFormat.format(Date(item.allocation.timestamp))
    }

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("allocation_history_card_${item.allocation.id}"),
        shape = RoundedCornerShape(18.dp),
        tintColor = LiquidMint
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
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
                            .background(LiquidMint.copy(alpha = 0.20f))
                            .border(1.dp, LiquidMint.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = LiquidMint,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Text(
                            text = dateString,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "Received: ${formatCurrency(item.allocation.totalAmount, currency)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF8FAFC)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (item.allocation.note.isNotBlank()) {
                Text(
                    text = "Note: ${item.allocation.note}",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1)
                )
            }

            HorizontalDivider(
                color = Color(0x26FFFFFF),
                thickness = 0.8.dp
            )

            // Splits breakdown
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item.splits.forEach { split ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = split.categoryName,
                            fontSize = 13.sp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "${formatCurrency(split.amount, currency)} (${split.percentage.toInt()}%)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LiquidMint
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseHistoryCard(
    item: ExpenseEntity,
    currency: String,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("d MMMM yyyy, h:mm a", Locale.getDefault()) }
    val dateString = remember(item.timestamp) {
        dateFormat.format(Date(item.timestamp))
    }

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_history_card_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        tintColor = LiquidRose
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(LiquidRose.copy(alpha = 0.20f))
                        .border(1.dp, LiquidRose.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = LiquidRose,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = item.description.ifBlank { "Expense" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
                    )
                    Text(
                        text = "${item.categoryName} • $dateString",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-${formatCurrency(item.amount, currency)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiquidRose
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete expense",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
