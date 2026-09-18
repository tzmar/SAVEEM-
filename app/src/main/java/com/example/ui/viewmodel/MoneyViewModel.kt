package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AllocationEntity
import com.example.data.model.AllocationSplitEntity
import com.example.data.model.AllocationWithSplits
import com.example.data.model.CategoryEntity
import com.example.data.model.CategorySummary
import com.example.data.model.ExpenseEntity
import com.example.data.model.GoalProgress
import com.example.data.model.OverallStats
import com.example.data.model.SplitPreview
import com.example.data.repository.MoneyRepository
import com.example.ui.theme.AppThemeMode
import com.example.util.GoalNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs

data class AllocationSuccessEvent(
    val allocation: AllocationEntity,
    val splits: List<AllocationSplitEntity>,
    val currency: String
)

class MoneyViewModel(private val repository: MoneyRepository) : ViewModel() {

    // App Theme State
    private val _themeMode = MutableStateFlow(AppThemeMode.LIQUID_DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    // Set of notified goal milestone keys formatted as "${goalId}_${milestonePercent}"
    private val notifiedMilestones = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            repository.ensureDefaultDataLoaded()
        }
    }

    fun checkAndTriggerGoalMilestones(context: Context, goals: List<GoalProgress>, currency: String) {
        val milestones = listOf(50, 75, 100)
        for (goal in goals) {
            val progressPercent = (goal.percentageComplete * 100).toInt()
            for (milestone in milestones) {
                val key = "${goal.id}_$milestone"
                if (progressPercent >= milestone && !notifiedMilestones.contains(key)) {
                    notifiedMilestones.add(key)
                    GoalNotificationHelper.showMilestoneNotification(
                        context = context,
                        goalId = goal.id,
                        goalTitle = goal.title,
                        milestonePercent = milestone,
                        currentAmount = goal.currentAmount,
                        targetAmount = goal.targetAmount,
                        currencySymbol = currency
                    )
                }
            }
        }
    }

    val currencySymbol: StateFlow<String> = repository.currencySymbol
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "P")

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categorySummaries: StateFlow<List<CategorySummary>> = repository.categorySummaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allocationsWithSplits: StateFlow<List<AllocationWithSplits>> = repository.allocationsWithSplits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.expenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goalsWithProgress: StateFlow<List<GoalProgress>> = repository.goalsWithProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val overallStats: StateFlow<OverallStats> = repository.overallStats
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            OverallStats(0.0, 0.0, 0.0, 0.0)
        )

    // Core screen input states
    private val _amountInput = MutableStateFlow("")
    val amountInput: StateFlow<String> = _amountInput.asStateFlow()

    private val _noteInput = MutableStateFlow("")
    val noteInput: StateFlow<String> = _noteInput.asStateFlow()

    private val _allocationSuccessEvent = MutableStateFlow<AllocationSuccessEvent?>(null)
    val allocationSuccessEvent: StateFlow<AllocationSuccessEvent?> = _allocationSuccessEvent.asStateFlow()

    // Real-time split preview
    val splitPreviews: StateFlow<List<SplitPreview>> = combine(
        _amountInput,
        categories
    ) { input, cats ->
        val amount = input.toDoubleOrNull() ?: 0.0
        if (amount > 0.0) {
            repository.calculateSplits(amount, cats)
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expense Recording Dialog state
    private val _expenseCategory = MutableStateFlow<CategorySummary?>(null)
    val expenseCategory: StateFlow<CategorySummary?> = _expenseCategory.asStateFlow()

    private val _expenseAmountInput = MutableStateFlow("")
    val expenseAmountInput: StateFlow<String> = _expenseAmountInput.asStateFlow()

    private val _expenseDescInput = MutableStateFlow("")
    val expenseDescInput: StateFlow<String> = _expenseDescInput.asStateFlow()

    // Add Goal Dialog state
    private val _isAddGoalOpen = MutableStateFlow(false)
    val isAddGoalOpen: StateFlow<Boolean> = _isAddGoalOpen.asStateFlow()

    private val _newGoalTitle = MutableStateFlow("")
    val newGoalTitle: StateFlow<String> = _newGoalTitle.asStateFlow()

    private val _newGoalTarget = MutableStateFlow("")
    val newGoalTarget: StateFlow<String> = _newGoalTarget.asStateFlow()

    private val _newGoalCategoryId = MutableStateFlow<Long?>(null)
    val newGoalCategoryId: StateFlow<Long?> = _newGoalCategoryId.asStateFlow()

    // Settings editable categories state
    private val _editableCategories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val editableCategories: StateFlow<List<CategoryEntity>> = _editableCategories.asStateFlow()

    private val _settingsFeedbackMessage = MutableStateFlow<String?>(null)
    val settingsFeedbackMessage: StateFlow<String?> = _settingsFeedbackMessage.asStateFlow()

    fun onAmountChanged(newInput: String) {
        // Allow only digits and at most one decimal point
        val clean = newInput.filter { it.isDigit() || it == '.' }
        if (clean.count { it == '.' } <= 1) {
            _amountInput.value = clean
        }
    }

    fun onQuickAmountSelected(amount: Double) {
        _amountInput.value = if (amount % 1.0 == 0.0) {
            amount.toLong().toString()
        } else {
            amount.toString()
        }
    }

    fun onNoteChanged(newNote: String) {
        _noteInput.value = newNote
    }

    fun allocateMoney() {
        val amount = _amountInput.value.toDoubleOrNull() ?: return
        if (amount <= 0.0) return

        viewModelScope.launch {
            val (savedAllocation, savedSplits) = repository.allocateMoney(amount, _noteInput.value)
            _allocationSuccessEvent.value = AllocationSuccessEvent(
                allocation = savedAllocation,
                splits = savedSplits,
                currency = currencySymbol.value
            )
            // Reset input for next entry
            _amountInput.value = ""
            _noteInput.value = ""
        }
    }

    fun dismissAllocationDialog() {
        _allocationSuccessEvent.value = null
    }

    // Expense Tracking
    fun openExpenseDialog(category: CategorySummary) {
        _expenseCategory.value = category
        _expenseAmountInput.value = ""
        _expenseDescInput.value = ""
    }

    fun dismissExpenseDialog() {
        _expenseCategory.value = null
        _expenseAmountInput.value = ""
        _expenseDescInput.value = ""
    }

    fun onExpenseAmountChanged(input: String) {
        val clean = input.filter { it.isDigit() || it == '.' }
        if (clean.count { it == '.' } <= 1) {
            _expenseAmountInput.value = clean
        }
    }

    fun onExpenseDescChanged(desc: String) {
        _expenseDescInput.value = desc
    }

    fun recordExpense() {
        val cat = _expenseCategory.value ?: return
        val amount = _expenseAmountInput.value.toDoubleOrNull() ?: return
        if (amount <= 0.0) return
        val desc = _expenseDescInput.value.ifBlank { "Spent from ${cat.name}" }

        viewModelScope.launch {
            repository.recordExpense(
                categoryId = cat.id,
                amount = amount,
                description = desc
            )
            dismissExpenseDialog()
        }
    }

    // Goal Management
    fun openAddGoalDialog() {
        _newGoalTitle.value = ""
        _newGoalTarget.value = ""
        _newGoalCategoryId.value = categories.value.firstOrNull()?.id
        _isAddGoalOpen.value = true
    }

    fun dismissAddGoalDialog() {
        _isAddGoalOpen.value = false
    }

    fun onGoalTitleChanged(title: String) {
        _newGoalTitle.value = title
    }

    fun onGoalTargetChanged(target: String) {
        val clean = target.filter { it.isDigit() || it == '.' }
        if (clean.count { it == '.' } <= 1) {
            _newGoalTarget.value = clean
        }
    }

    fun onGoalCategorySelected(categoryId: Long?) {
        _newGoalCategoryId.value = categoryId
    }

    fun saveNewGoal() {
        val title = _newGoalTitle.value.trim()
        val target = _newGoalTarget.value.toDoubleOrNull() ?: return
        if (title.isBlank() || target <= 0.0) return

        viewModelScope.launch {
            repository.addGoal(
                title = title,
                targetAmount = target,
                linkedCategoryId = _newGoalCategoryId.value
            )
            dismissAddGoalDialog()
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
        }
    }

    // Settings & Category Editing
    fun loadEditableCategories() {
        _editableCategories.value = categories.value.map { it.copy() }
        _settingsFeedbackMessage.value = null
    }

    fun updateEditableCategory(index: Int, name: String, percentage: Double, description: String) {
        val list = _editableCategories.value.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(
                name = name,
                percentage = percentage,
                description = description
            )
            _editableCategories.value = list
        }
    }

    fun addEditableCategory(name: String, percentage: Double, description: String, colorHex: String) {
        val list = _editableCategories.value.toMutableList()
        list.add(
            CategoryEntity(
                name = name.ifBlank { "New Category" },
                percentage = percentage,
                description = description,
                colorHex = colorHex,
                displayOrder = list.size
            )
        )
        _editableCategories.value = list
    }

    fun removeEditableCategory(index: Int) {
        val list = _editableCategories.value.toMutableList()
        if (index in list.indices && list.size > 1) {
            list.removeAt(index)
            _editableCategories.value = list
        }
    }

    fun saveCategoryPercentages(): Boolean {
        val current = _editableCategories.value
        val totalPercentage = current.sumOf { it.percentage }

        // Must be exactly 100% (with minor tolerance for 99.99/100.01)
        if (abs(totalPercentage - 100.0) > 0.01) {
            _settingsFeedbackMessage.value = "Percentages must total exactly 100%! Current sum: ${String.format("%.1f", totalPercentage)}%"
            return false
        }

        viewModelScope.launch {
            repository.updateCategories(current)
            _settingsFeedbackMessage.value = "Saved successfully! Allocations set to 100%."
        }
        return true
    }

    fun setCurrency(symbol: String) {
        viewModelScope.launch {
            repository.setCurrency(symbol)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            loadEditableCategories()
            _settingsFeedbackMessage.value = "All data reset to initial defaults."
        }
    }

    fun exportData(context: Context) {
        viewModelScope.launch {
            val csv = repository.exportCsvData()
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csv)
                putExtra(Intent.EXTRA_SUBJECT, "Money Allocator Transaction History.csv")
                type = "text/csv"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Export Money History (CSV)")
            context.startActivity(shareIntent)
        }
    }

    fun deleteAllocation(id: Long) {
        viewModelScope.launch {
            repository.deleteAllocation(id)
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }
}

class MoneyViewModelFactory(private val repository: MoneyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoneyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoneyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
