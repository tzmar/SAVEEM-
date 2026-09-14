package com.example.data.model

data class CategorySummary(
    val id: Long,
    val name: String,
    val percentage: Double,
    val description: String,
    val colorHex: String,
    val totalAllocated: Double,
    val totalSpent: Double,
    val currentBalance: Double
)

data class GoalProgress(
    val id: Long,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val percentageComplete: Double,
    val remainingAmount: Double,
    val linkedCategoryId: Long?,
    val linkedCategoryName: String?
)

data class OverallStats(
    val totalReceived: Double,
    val totalAllocated: Double,
    val totalSavedFuture: Double,
    val totalSpent: Double
)

data class SplitPreview(
    val categoryId: Long,
    val categoryName: String,
    val percentage: Double,
    val amount: Double,
    val colorHex: String
)
