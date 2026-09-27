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

/**
 * Currency pair for Forex trading / currency conversion.
 * Allows user to set their custom exchange rate (e.g. 1 USD = 15.00 Pula BWP).
 */
data class CurrencyPair(
    val id: String,           // e.g. "USD_BWP"
    val baseCode: String,     // e.g. "USD"
    val baseSymbol: String,   // e.g. "$"
    val targetCode: String,   // e.g. "BWP"
    val targetSymbol: String, // e.g. "P"
    val defaultRate: Double,  // e.g. 14.0
    val isCustom: Boolean = false
)

data class ForexConversionResult(
    val baseAmount: Double,
    val targetAmount: Double,
    val rate: Double,
    val pair: CurrencyPair
)
