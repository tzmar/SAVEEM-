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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                Text(
                    text = "CONTROL PANEL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Financial Balances & Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Top 4 High-Level Metrics
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Received",
                    value = formatCurrency(stats.totalReceived, currency),
                    icon = Icons.Default.TrendingUp,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Allocated",
                    value = formatCurrency(stats.totalAllocated, currency),
                    icon = Icons.Default.ArrowDownward,
                    accentColor = Color(0xFF0284C7),
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
                    accentColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Spent",
                    value = formatCurrency(stats.totalSpent, currency),
                    icon = Icons.Default.TrendingDown,
                    accentColor = Color(0xFFF43F5E),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Allocated − Spent",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Major Goals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.openAddGoalDialog() },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_goal_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Goal", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (goals.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No goals created yet.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Create goals like 'Emergency Fund' or 'Tools Fund' to track progress.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
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

        Spacer(modifier = Modifier.height(20.dp))
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
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
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
                color = MaterialTheme.colorScheme.onSurface
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_card_${category.id}"),
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header row
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
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(catColor)
                    )
                    Text(
                        text = category.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = catColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${category.percentage.toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = catColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (category.description.isNotBlank()) {
                Text(
                    text = category.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatCurrency(category.currentBalance, currency),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (category.currentBalance >= 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                    )
                }

                // Quick Spend Button (Secondary tracking)
                OutlinedButton(
                    onClick = onSpendClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("spend_button_${category.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Spend",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Sub-metrics: Total Allocated and Total Spent
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Allocated: ${formatCurrency(category.totalAllocated, currency)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Spent: ${formatCurrency(category.totalSpent, currency)}",
                    fontSize = 11.sp,
                    color = if (category.totalSpent > 0) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_card_${goal.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    goal.linkedCategoryName?.let { catName ->
                        Text(
                            text = "Linked to $catName",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${String.format("%.1f", goal.percentageComplete)}% complete",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = { (goal.percentageComplete / 100.0).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Remaining amount subtext
            Text(
                text = if (goal.remainingAmount > 0) {
                    "${formatCurrency(goal.remainingAmount, currency)} remaining to reach goal"
                } else {
                    "🎉 Target achieved!"
                },
                fontSize = 11.sp,
                color = if (goal.remainingAmount > 0) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF10B981),
                fontWeight = if (goal.remainingAmount > 0) FontWeight.Normal else FontWeight.Bold
            )
        }
    }
}
