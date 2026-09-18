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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategorySummary
import com.example.data.model.GoalProgress
import com.example.ui.components.AddGoalDialog
import com.example.ui.components.LiquidCyan
import com.example.ui.components.LiquidEmerald
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassPill
import com.example.ui.components.LiquidMint
import com.example.ui.components.LiquidRose
import com.example.ui.components.RecordExpenseDialog
import com.example.ui.components.formatCurrency
import com.example.ui.components.parseColorSafe
import com.example.ui.viewmodel.MoneyViewModel

@Composable
fun DashboardScreen(
    viewModel: MoneyViewModel,
    modifier: Modifier = Modifier
) {
    val currency by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val summaries by viewModel.categorySummaries.collectAsStateWithLifecycle()
    val stats by viewModel.overallStats.collectAsStateWithLifecycle()
    val goals by viewModel.goalsWithProgress.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Dashboard Title
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
                            .background(LiquidCyan)
                    )
                    Text(
                        text = "LIQUID DASHBOARD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = LiquidCyan
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Financial Balances & Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC)
                )
            }
        }

        // Top 4 High-Level Metrics (Liquid Glass Stat Cards)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Received",
                    value = formatCurrency(stats.totalReceived, currency),
                    icon = Icons.Default.TrendingUp,
                    accentColor = LiquidMint,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Allocated",
                    value = formatCurrency(stats.totalAllocated, currency),
                    icon = Icons.Default.ArrowDownward,
                    accentColor = LiquidCyan,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Saved For Future",
                    value = formatCurrency(stats.totalSavedFuture, currency),
                    icon = Icons.Default.Savings,
                    accentColor = LiquidEmerald,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Spent",
                    value = formatCurrency(stats.totalSpent, currency),
                    icon = Icons.Default.TrendingDown,
                    accentColor = LiquidRose,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Four Main Balances
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category Balances",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC)
                )
                Text(
                    text = "Allocated − Spent",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
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

        // Section: Major Goals with Simple Progress Bars
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
                        tint = LiquidMint,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Major Goals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x2238BDF8))
                        .border(
                            1.dp,
                            Brush.verticalGradient(listOf(Color(0x8038BDF8), Color(0x2038BDF8))),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.openAddGoalDialog() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("add_goal_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = LiquidCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "New Goal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LiquidCyan
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
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No goals created yet.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFF8FAFC)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create goals like 'Emergency Fund' or 'Tools Fund' to track progress.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
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

        Spacer(modifier = Modifier.height(24.dp))
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
                    color = Color(0xFF94A3B8)
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
                color = Color(0xFFF8FAFC)
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
    val catColor = parseColorSafe(category.colorHex)

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_card_${category.id}"),
        shape = RoundedCornerShape(22.dp),
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
                            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                    )
                    Text(
                        text = category.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
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
                    color = Color(0xFF94A3B8)
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
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCurrency(category.currentBalance, currency),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (category.currentBalance >= 0) Color(0xFFF8FAFC) else LiquidRose
                    )
                }

                // Quick Spend Button (Frosted Liquid Glass Button)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x35F43F5E), Color(0x18F43F5E))
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
                        .padding(horizontal = 14.dp, vertical = 9.dp)
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
                            color = Color(0xFFF8FAFC)
                        )
                    }
                }
            }

            // Sub-metrics: Total Allocated and Total Spent
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x20000000))
                    .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Allocated: ${formatCurrency(category.totalAllocated, currency)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "Spent: ${formatCurrency(category.totalSpent, currency)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (category.totalSpent > 0) LiquidRose else Color(0xFF94A3B8)
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
                        color = Color(0xFFF8FAFC)
                    )
                    goal.linkedCategoryName?.let { catName ->
                        Text(
                            text = "Linked to $catName",
                            fontSize = 11.sp,
                            color = LiquidCyan
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
                        tint = Color(0xFF94A3B8),
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
                    color = Color(0xFFF8FAFC)
                )
                Text(
                    text = "${String.format("%.1f", goal.percentageComplete)}% complete",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LiquidMint
                )
            }

            // Liquid Glass Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x30FFFFFF))
                    .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(4.dp))
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
                color = if (goal.remainingAmount > 0) Color(0xFF94A3B8) else LiquidMint,
                fontWeight = if (goal.remainingAmount > 0) FontWeight.Normal else FontWeight.Bold
            )
        }
    }
}
