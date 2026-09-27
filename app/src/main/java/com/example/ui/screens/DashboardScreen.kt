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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AllocationWithSplits
import com.example.data.model.CategorySummary
import com.example.data.model.ExpenseEntity
import com.example.data.model.GoalProgress
import com.example.ui.components.AddGoalDialog
import com.example.ui.components.LiquidCyan
import com.example.ui.components.LiquidEmerald
import com.example.ui.components.LiquidGlassButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassPill
import com.example.ui.components.LiquidMint
import com.example.ui.components.LiquidRose
import com.example.ui.components.LiquidTeal
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.components.RecordExpenseDialog
import com.example.ui.components.formatCurrency
import com.example.ui.components.parseColorSafe
import com.example.ui.viewmodel.MoneyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: MoneyViewModel,
    onAddIncomeClick: () -> Unit = {},
    onOpenForexClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val summaries by viewModel.categorySummaries.collectAsStateWithLifecycle()
    val stats by viewModel.overallStats.collectAsStateWithLifecycle()
    val goals by viewModel.goalsWithProgress.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val allocations by viewModel.allocationsWithSplits.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()

    // Expense dialog state
    val expenseCategory by viewModel.expenseCategory.collectAsStateWithLifecycle()
    val expenseAmount by viewModel.expenseAmountInput.collectAsStateWithLifecycle()
    val expenseDesc by viewModel.expenseDescInput.collectAsStateWithLifecycle()

    // Add goal dialog state
    val isAddGoalOpen by viewModel.isAddGoalOpen.collectAsStateWithLifecycle()
    val newGoalTitle by viewModel.newGoalTitle.collectAsStateWithLifecycle()
    val newGoalTarget by viewModel.newGoalTarget.collectAsStateWithLifecycle()
    val newGoalCatId by viewModel.newGoalCategoryId.collectAsStateWithLifecycle()

    // Expense modal dialog
    expenseCategory?.let { cat ->
        RecordExpenseDialog(
            category = cat,
            currency = currency,
            amount = expenseAmount,
            description = expenseDesc,
            onAmountChange = viewModel::onExpenseAmountChanged,
            onDescriptionChange = viewModel::onExpenseDescChanged,
            onConfirm = viewModel::recordExpense,
            onDismiss = viewModel::dismissExpenseDialog
        )
    }

    // Add Goal dialog
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

    val totalCurrentBalance = remember(summaries) {
        summaries.sumOf { it.currentBalance }
    }

    // Recent combined activities (last 5)
    val recentActivities = remember(allocations, expenses) {
        val list = mutableListOf<RecentActivityItem>()
        list.addAll(allocations.map { RecentActivityItem.Income(it) })
        list.addAll(expenses.map { RecentActivityItem.Spent(it) })
        list.sortByDescending { it.timestamp }
        list.take(5)
    }

    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0A0F1D)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // App Header Brand
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(LiquidMint)
                    )
                    Text(
                        text = "SAVEEM",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.4.sp,
                        color = if (isDark) LiquidMint else Color(0xFF0D9488)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Money Control & Allocation",
                    fontSize = 14.sp,
                    color = textColorSecondary
                )
            }

            LiquidGlassPill(
                text = "Currency: $currency",
                color = LiquidCyan
            )
        }

        // 1. PRIMARY TOTAL BALANCE & HERO CARD
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("primary_balance_card"),
            shape = RoundedCornerShape(26.dp),
            tintColor = LiquidMint
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL AVAILABLE BALANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                        color = textColorSecondary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0x2034D399) else Color(0x200D9488))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Live",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) LiquidMint else Color(0xFF0D9488)
                        )
                    }
                }

                Text(
                    text = formatCurrency(totalCurrentBalance, currency),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    color = textColorPrimary
                )

                // High-Level Metrics Pill Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0x20000000) else Color(0x0A0F172A))
                        .border(
                            1.dp,
                            if (isDark) Color(0x1FFFFFFF) else Color(0x100F172A),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Received",
                            fontSize = 11.sp,
                            color = textColorSecondary
                        )
                        Text(
                            text = formatCurrency(stats.totalReceived, currency),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) LiquidMint else Color(0xFF059669)
                        )
                    }
                    Column {
                        Text(
                            text = "Allocated",
                            fontSize = 11.sp,
                            color = textColorSecondary
                        )
                        Text(
                            text = formatCurrency(stats.totalAllocated, currency),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) LiquidCyan else Color(0xFF0284C7)
                        )
                    }
                    Column {
                        Text(
                            text = "Spent",
                            fontSize = 11.sp,
                            color = textColorSecondary
                        )
                        Text(
                            text = formatCurrency(stats.totalSpent, currency),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) LiquidRose else Color(0xFFE11D48)
                        )
                    }
                }

                // Obvious "Add Income" Primary Action Button
                LiquidGlassButton(
                    onClick = onAddIncomeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("home_add_income_button"),
                    gradientColors = listOf(Color(0xFF0EA5E9), Color(0xFF0D9488), Color(0xFF10B981))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Income & Allocate",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Quick Forex / Currency Converter Action Banner
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenForexClick() }
                .testTag("forex_banner_card"),
            shape = RoundedCornerShape(16.dp),
            tintColor = LiquidCyan
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x2806B6D4) else Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyExchange,
                            contentDescription = null,
                            tint = if (isDark) LiquidCyan else Color(0xFF0284C7),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Forex & Currency Converter",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorPrimary
                        )
                        Text(
                            text = "Convert USD/Pula at custom rates & allocate",
                            fontSize = 12.sp,
                            color = textColorSecondary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = if (isDark) LiquidCyan else Color(0xFF0284C7),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // 2. YOUR MONEY (Category Breakdown with Spend & Balance)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = null,
                        tint = if (isDark) LiquidMint else Color(0xFF0D9488),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "YOUR MONEY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = textColorPrimary
                    )
                }
                Text(
                    text = "Allocated − Spent",
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
            }

            summaries.forEach { category ->
                CategoryBalanceCard(
                    category = category,
                    currency = currency,
                    onSpendClick = { viewModel.openExpenseDialog(category) }
                )
            }
        }

        // 3. YOUR GOALS
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = if (isDark) LiquidCyan else Color(0xFF0284C7),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "YOUR GOALS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = textColorPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0x2238BDF8) else Color(0x1A0284C7))
                        .border(
                            1.dp,
                            if (isDark) Color(0x6638BDF8) else Color(0x330284C7),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.openAddGoalDialog() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("add_goal_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = if (isDark) LiquidCyan else Color(0xFF0284C7),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "New Goal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) LiquidCyan else Color(0xFF0284C7)
                        )
                    }
                }
            }

            if (goals.isEmpty()) {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No active goals yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColorPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Set targets like 'Emergency Reserve' or 'Tools Upgrade' to track progress.",
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
        }

        // 4. RECENT ACTIVITY
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ACTIVITY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = textColorPrimary
                )
                Text(
                    text = "Latest Allocations & Expenses",
                    fontSize = 11.sp,
                    color = textColorSecondary
                )
            }

            if (recentActivities.isEmpty()) {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions recorded yet.",
                            fontSize = 13.sp,
                            color = textColorSecondary
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    recentActivities.forEach { item ->
                        RecentActivityCard(
                            item = item,
                            currency = currency
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

sealed class RecentActivityItem {
    abstract val timestamp: Long

    data class Income(val data: AllocationWithSplits) : RecentActivityItem() {
        override val timestamp: Long get() = data.allocation.timestamp
    }

    data class Spent(val data: ExpenseEntity) : RecentActivityItem() {
        override val timestamp: Long get() = data.timestamp
    }
}

@Composable
fun RecentActivityCard(
    item: RecentActivityItem,
    currency: String
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    when (item) {
        is RecentActivityItem.Income -> {
            val alloc = item.data.allocation
            val splits = item.data.splits
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tintColor = LiquidMint
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0x2034D399) else Color(0x200D9488)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = if (isDark) LiquidMint else Color(0xFF0D9488),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (alloc.note.isNotBlank()) alloc.note else "Income Allocated",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColorPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${splits.size} categories • ${dateFormat.format(Date(alloc.timestamp))}",
                                fontSize = 11.sp,
                                color = textColorSecondary
                            )
                        }
                    }

                    Text(
                        text = "+${formatCurrency(alloc.totalAmount, currency)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) LiquidMint else Color(0xFF059669)
                    )
                }
            }
        }

        is RecentActivityItem.Spent -> {
            val expense = item.data
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tintColor = LiquidRose
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0x20F43F5E) else Color(0x20E11D48)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = if (isDark) LiquidRose else Color(0xFFE11D48),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (expense.description.isNotBlank()) expense.description else "Expense Recorded",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColorPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${expense.categoryName} • ${dateFormat.format(Date(expense.timestamp))}",
                                fontSize = 11.sp,
                                color = textColorSecondary
                            )
                        }
                    }

                    Text(
                        text = "-${formatCurrency(expense.amount, currency)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) LiquidRose else Color(0xFFE11D48)
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    LiquidGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        tintColor = accentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorSecondary
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.20f))
                        .border(1.dp, accentColor.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Text(
                text = value,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColorPrimary
            )
        }
    }
}

@Composable
fun CategoryBalanceCard(
    category: CategorySummary,
    currency: String,
    onSpendClick: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val catColor = parseColorSafe(category.colorHex)
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_card_${category.id}"),
        shape = RoundedCornerShape(20.dp),
        tintColor = catColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(catColor)
                            .border(1.dp, if (isDark) Color.White.copy(alpha = 0.6f) else Color(0x330F172A), CircleShape)
                    )
                    Text(
                        text = category.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                }

                LiquidGlassPill(
                    text = "${category.percentage.toInt()}%",
                    color = catColor
                )
            }

            if (category.description.isNotBlank()) {
                Text(
                    text = category.description,
                    fontSize = 12.sp,
                    color = textColorSecondary
                )
            }

            // Large Balance Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "AVAILABLE BALANCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp,
                        color = textColorSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCurrency(category.currentBalance, currency),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (category.currentBalance >= 0) textColorPrimary else LiquidRose
                    )
                }

                // Quick Spend Button (Frosted Liquid Glass Button)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                if (isDark) {
                                    listOf(Color(0x35F43F5E), Color(0x18F43F5E))
                                } else {
                                    listOf(Color(0x20F43F5E), Color(0x10F43F5E))
                                }
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(Color(0x80F43F5E), Color(0x20F43F5E))
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onSpendClick)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("spend_button_${category.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = LiquidRose,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Spend",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFF8FAFC) else Color(0xFFE11D48)
                        )
                    }
                }
            }

            // Sub-metrics: Total Allocated and Total Spent
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0x20000000) else Color(0x0A0F172A))
                    .border(
                        1.dp,
                        if (isDark) Color(0x20FFFFFF) else Color(0x100F172A),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Allocated: ${formatCurrency(category.totalAllocated, currency)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColorSecondary
                )
                Text(
                    text = "Spent: ${formatCurrency(category.totalSpent, currency)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (category.totalSpent > 0) LiquidRose else textColorSecondary
                )
            }
        }
    }
}

@Composable
fun GoalProgressCard(
    goal: GoalProgress,
    currency: String,
    onDelete: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val textColorPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textColorSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_card_${goal.id}"),
        shape = RoundedCornerShape(18.dp),
        tintColor = LiquidMint
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColorPrimary
                    )
                    goal.linkedCategoryName?.let { catName ->
                        Text(
                            text = "Linked to $catName",
                            fontSize = 11.sp,
                            color = if (isDark) LiquidCyan else Color(0xFF0284C7)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Goal",
                        tint = textColorSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Amounts and percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${formatCurrency(goal.currentAmount, currency)} / ${formatCurrency(goal.targetAmount, currency)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )
                Text(
                    text = "${String.format("%.1f", goal.percentageComplete)}% complete",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) LiquidMint else Color(0xFF059669)
                )
            }

            // Liquid Glass Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isDark) Color(0x30FFFFFF) else Color(0x200F172A))
                    .border(
                        0.5.dp,
                        if (isDark) Color(0x33FFFFFF) else Color(0x150F172A),
                        RoundedCornerShape(4.dp)
                    )
            ) {
                val progressFraction = (goal.percentageComplete / 100.0).toFloat().coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(LiquidCyan, LiquidMint)
                            )
                        )
                )
            }

            // Remaining amount subtext
            Text(
                text = if (goal.remainingAmount > 0) {
                    "${formatCurrency(goal.remainingAmount, currency)} remaining to reach goal"
                } else {
                    "🎉 Target achieved!"
                },
                fontSize = 11.sp,
                color = if (goal.remainingAmount > 0) textColorSecondary else (if (isDark) LiquidMint else Color(0xFF059669)),
                fontWeight = if (goal.remainingAmount > 0) FontWeight.Normal else FontWeight.Bold
            )
        }
    }
}
