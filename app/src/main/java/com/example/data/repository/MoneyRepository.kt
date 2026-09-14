package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.AllocationEntity
import com.example.data.model.AllocationSplitEntity
import com.example.data.model.AllocationWithSplits
import com.example.data.model.AppSettingEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CategorySummary
import com.example.data.model.ExpenseEntity
import com.example.data.model.GoalEntity
import com.example.data.model.GoalProgress
import com.example.data.model.OverallStats
import com.example.data.model.SplitPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToLong

class MoneyRepository(private val database: AppDatabase) {

    private val categoryDao = database.categoryDao()
    private val allocationDao = database.allocationDao()
    private val expenseDao = database.expenseDao()
    private val goalDao = database.goalDao()
    private val settingsDao = database.settingsDao()

    val categories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    val allocationsWithSplits: Flow<List<AllocationWithSplits>> =
        allocationDao.getAllAllocationsWithSplits()

    val expenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    val currencySymbol: Flow<String> = settingsDao.getSetting("currency_symbol")
        .map { it?.ifBlank { "P" } ?: "P" }
        .distinctUntilChanged()

    suspend fun ensureDefaultDataLoaded() = withContext(Dispatchers.IO) {
        val existing = categoryDao.getCategoriesList()
        if (existing.isEmpty()) {
            val defaultCategories = listOf(
                CategoryEntity(
                    name = "Future / Zimbabwe Fund",
                    percentage = 40.0,
                    description = "Savings, investments & capital",
                    colorHex = "#10B981",
                    displayOrder = 0
                ),
                CategoryEntity(
                    name = "Tools & Skills",
                    percentage = 30.0,
                    description = "Equipment, tools, courses & knowledge",
                    colorHex = "#F59E0B",
                    displayOrder = 1
                ),
                CategoryEntity(
                    name = "Personal / Fun",
                    percentage = 20.0,
                    description = "Living, enjoyment & personal needs",
                    colorHex = "#6366F1",
                    displayOrder = 2
                ),
                CategoryEntity(
                    name = "Family / Miscellaneous",
                    percentage = 10.0,
                    description = "Family support & emergency buffer",
                    colorHex = "#EC4899",
                    displayOrder = 3
                )
            )
            categoryDao.insertCategories(defaultCategories)
            settingsDao.setSetting(AppSettingEntity(key = "currency_symbol", value = "P"))

            val cats = categoryDao.getCategoriesList()
            val futureCat = cats.find { it.name.contains("Future", ignoreCase = true) }
            val toolsCat = cats.find { it.name.contains("Tools", ignoreCase = true) }
            val famCat = cats.find { it.name.contains("Family", ignoreCase = true) }

            goalDao.insertGoals(
                listOf(
                    GoalEntity(
                        title = "Zimbabwe Business Capital",
                        targetAmount = 10000.0,
                        linkedCategoryId = futureCat?.id
                    ),
                    GoalEntity(
                        title = "Tools Fund",
                        targetAmount = 5000.0,
                        linkedCategoryId = toolsCat?.id
                    ),
                    GoalEntity(
                        title = "Emergency Fund",
                        targetAmount = 2000.0,
                        linkedCategoryId = famCat?.id
                    )
                )
            )
        }
    }

    val categorySummaries: Flow<List<CategorySummary>> = combine(
        categoryDao.getAllCategories(),
        allocationDao.getAllSplits(),
        expenseDao.getAllExpenses()
    ) { categoriesList, allSplits, allExpenses ->
        categoriesList.map { category ->
            val totalAllocated = allSplits
                .filter { it.categoryId == category.id }
                .sumOf { it.amount }
            val totalSpent = allExpenses
                .filter { it.categoryId == category.id }
                .sumOf { it.amount }
            val currentBalance = totalAllocated - totalSpent

            CategorySummary(
                id = category.id,
                name = category.name,
                percentage = category.percentage,
                description = category.description,
                colorHex = category.colorHex,
                totalAllocated = totalAllocated,
                totalSpent = totalSpent,
                currentBalance = currentBalance
            )
        }
    }

    val goalsWithProgress: Flow<List<GoalProgress>> = combine(
        goalDao.getAllGoals(),
        categorySummaries
    ) { goalsList, summaries ->
        goalsList.map { goal ->
            val linkedCategory = summaries.find { it.id == goal.linkedCategoryId }
            val currentAmount = if (linkedCategory != null) {
                // If linked to category, uses category's accumulated current balance (or at least 0)
                max(0.0, linkedCategory.currentBalance)
            } else {
                goal.manualCurrentAmount
            }
            val percentage = if (goal.targetAmount > 0) {
                ((currentAmount / goal.targetAmount) * 100.0).coerceIn(0.0, 100.0)
            } else 0.0
            val remaining = max(0.0, goal.targetAmount - currentAmount)

            GoalProgress(
                id = goal.id,
                title = goal.title,
                targetAmount = goal.targetAmount,
                currentAmount = currentAmount,
                percentageComplete = percentage,
                remainingAmount = remaining,
                linkedCategoryId = goal.linkedCategoryId,
                linkedCategoryName = linkedCategory?.name
            )
        }
    }

    val overallStats: Flow<OverallStats> = combine(
        allocationDao.getAllAllocationsWithSplits(),
        expenseDao.getAllExpenses(),
        categorySummaries
    ) { allocations, expensesList, summaries ->
        val totalReceived = allocations.sumOf { it.allocation.totalAmount }
        val totalAllocated = allocations.flatMap { it.splits }.sumOf { it.amount }
        val totalSpent = expensesList.sumOf { it.amount }

        // Future/Zimbabwe Fund saved amount (identified by name or first category if contains Future/Zimbabwe/Savings)
        val futureCat = summaries.find {
            it.name.contains("Future", ignoreCase = true) ||
                    it.name.contains("Zimbabwe", ignoreCase = true) ||
                    it.name.contains("Save", ignoreCase = true)
        } ?: summaries.firstOrNull()

        val totalSavedFuture = futureCat?.currentBalance ?: 0.0

        OverallStats(
            totalReceived = totalReceived,
            totalAllocated = totalAllocated,
            totalSavedFuture = totalSavedFuture,
            totalSpent = totalSpent
        )
    }

    fun calculateSplits(amount: Double, categoriesList: List<CategoryEntity>): List<SplitPreview> {
        if (amount <= 0.0 || categoriesList.isEmpty()) return emptyList()

        val totalCents = (amount * 100.0).roundToLong()
        var remainingCents = totalCents
        val previews = mutableListOf<SplitPreview>()

        for (i in categoriesList.indices) {
            val cat = categoriesList[i]
            val splitCents = if (i == categoriesList.lastIndex) {
                remainingCents // Absorbs any penny/cent rounding rounding difference
            } else {
                val calculated = ((totalCents * cat.percentage) / 100.0).roundToLong()
                calculated.coerceAtMost(remainingCents)
            }
            remainingCents -= splitCents
            val splitAmount = splitCents / 100.0

            previews.add(
                SplitPreview(
                    categoryId = cat.id,
                    categoryName = cat.name,
                    percentage = cat.percentage,
                    amount = splitAmount,
                    colorHex = cat.colorHex
                )
            )
        }
        return previews
    }

    suspend fun allocateMoney(
        amount: Double,
        note: String = ""
    ): Pair<AllocationEntity, List<AllocationSplitEntity>> = withContext(Dispatchers.IO) {
        val cats = categoryDao.getCategoriesList()
        val previews = calculateSplits(amount, cats)

        val allocation = AllocationEntity(
            timestamp = System.currentTimeMillis(),
            totalAmount = amount,
            note = note.trim()
        )
        val allocationId = allocationDao.insertAllocation(allocation)
        val savedAllocation = allocation.copy(id = allocationId)

        val splits = previews.map { preview ->
            AllocationSplitEntity(
                allocationId = allocationId,
                categoryId = preview.categoryId,
                categoryName = preview.categoryName,
                amount = preview.amount,
                percentage = preview.percentage
            )
        }
        allocationDao.insertSplits(splits)
        Pair(savedAllocation, splits)
    }

    suspend fun recordExpense(
        categoryId: Long,
        amount: Double,
        description: String
    ): ExpenseEntity = withContext(Dispatchers.IO) {
        val cats = categoryDao.getCategoriesList()
        val catName = cats.find { it.id == categoryId }?.name ?: "Expense"
        val expense = ExpenseEntity(
            categoryId = categoryId,
            categoryName = catName,
            amount = amount,
            description = description.trim(),
            timestamp = System.currentTimeMillis()
        )
        val id = expenseDao.insertExpense(expense)
        expense.copy(id = id)
    }

    suspend fun deleteAllocation(id: Long) = withContext(Dispatchers.IO) {
        allocationDao.deleteAllocationById(id)
    }

    suspend fun deleteExpense(id: Long) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun updateCategories(newCategories: List<CategoryEntity>) = withContext(Dispatchers.IO) {
        // Enforce 100% total
        categoryDao.deleteAllCategories()
        categoryDao.insertCategories(newCategories)
    }

    suspend fun insertCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategory(category)
    }

    suspend fun addGoal(
        title: String,
        targetAmount: Double,
        linkedCategoryId: Long?
    ) = withContext(Dispatchers.IO) {
        goalDao.insertGoal(
            GoalEntity(
                title = title.trim(),
                targetAmount = targetAmount,
                linkedCategoryId = linkedCategoryId
            )
        )
    }

    suspend fun updateGoal(goal: GoalEntity) = withContext(Dispatchers.IO) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(id: Long) = withContext(Dispatchers.IO) {
        goalDao.deleteGoalById(id)
    }

    suspend fun setCurrency(symbol: String) = withContext(Dispatchers.IO) {
        settingsDao.setSetting(AppSettingEntity(key = "currency_symbol", value = symbol.trim()))
    }

    suspend fun resetAllData() = withContext(Dispatchers.IO) {
        allocationDao.deleteAllSplits()
        allocationDao.deleteAllAllocations()
        expenseDao.deleteAllExpenses()
        goalDao.deleteAllGoals()
        categoryDao.deleteAllCategories()

        // Re-populate default data
        val defaultCategories = listOf(
            CategoryEntity(
                name = "Future / Zimbabwe Fund",
                percentage = 40.0,
                description = "Savings, investments & capital",
                colorHex = "#10B981",
                displayOrder = 0
            ),
            CategoryEntity(
                name = "Tools & Skills",
                percentage = 30.0,
                description = "Equipment, tools, courses & knowledge",
                colorHex = "#F59E0B",
                displayOrder = 1
            ),
            CategoryEntity(
                name = "Personal / Fun",
                percentage = 20.0,
                description = "Living, enjoyment & personal needs",
                colorHex = "#6366F1",
                displayOrder = 2
            ),
            CategoryEntity(
                name = "Family / Miscellaneous",
                percentage = 10.0,
                description = "Family support & emergency buffer",
                colorHex = "#EC4899",
                displayOrder = 3
            )
        )
        categoryDao.insertCategories(defaultCategories)
        settingsDao.setSetting(AppSettingEntity(key = "currency_symbol", value = "P"))

        val cats = categoryDao.getCategoriesList()
        val futureCat = cats.find { it.name.contains("Future", ignoreCase = true) }
        val toolsCat = cats.find { it.name.contains("Tools", ignoreCase = true) }
        val famCat = cats.find { it.name.contains("Family", ignoreCase = true) }

        goalDao.insertGoals(
            listOf(
                GoalEntity(
                    title = "Zimbabwe Business Capital",
                    targetAmount = 10000.0,
                    linkedCategoryId = futureCat?.id
                ),
                GoalEntity(
                    title = "Tools Fund",
                    targetAmount = 5000.0,
                    linkedCategoryId = toolsCat?.id
                ),
                GoalEntity(
                    title = "Emergency Fund",
                    targetAmount = 2000.0,
                    linkedCategoryId = famCat?.id
                )
            )
        )
    }

    suspend fun exportCsvData(): String = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currency = currencySymbol.first()
        val allocations = allocationDao.getAllAllocationsWithSplits().first()
        val allExpenses = expenseDao.getAllExpenses().first()

        val sb = StringBuilder()
        sb.append("Date,Type,Amount,Currency,Details / Splits,Note\n")

        for (item in allocations) {
            val dateStr = dateFormat.format(Date(item.allocation.timestamp))
            val splitsStr = item.splits.joinToString(" | ") {
                "${it.categoryName}: $currency${String.format(Locale.US, "%.2f", it.amount)} (${it.percentage}%)"
            }
            sb.append("\"$dateStr\",\"INCOME ALLOCATION\",\"${String.format(Locale.US, "%.2f", item.allocation.totalAmount)}\",\"$currency\",\"$splitsStr\",\"${item.allocation.note.replace("\"", "\"\"")}\"\n")
        }

        for (exp in allExpenses) {
            val dateStr = dateFormat.format(Date(exp.timestamp))
            sb.append("\"$dateStr\",\"EXPENSE\",\"-${String.format(Locale.US, "%.2f", exp.amount)}\",\"$currency\",\"Category: ${exp.categoryName}\",\"${exp.description.replace("\"", "\"\"")}\"\n")
        }

        sb.toString()
    }
}
