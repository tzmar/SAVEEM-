package com.example.util

import com.example.data.model.AllocationEntity
import com.example.data.model.AllocationSplitEntity
import com.example.data.model.AppSettingEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.GoalEntity
import org.json.JSONArray
import org.json.JSONObject

data class BackupData(
    val version: Int,
    val appName: String,
    val exportedAt: Long,
    val categories: List<CategoryEntity>,
    val allocations: List<AllocationEntity>,
    val splits: List<AllocationSplitEntity>,
    val expenses: List<ExpenseEntity>,
    val goals: List<GoalEntity>,
    val settings: List<AppSettingEntity>
)

data class BackupSummary(
    val categoriesCount: Int,
    val allocationsCount: Int,
    val splitsCount: Int,
    val expensesCount: Int,
    val goalsCount: Int,
    val exportedAt: Long
)

object BackupManager {

    fun createBackupJson(
        categories: List<CategoryEntity>,
        allocations: List<AllocationEntity>,
        splits: List<AllocationSplitEntity>,
        expenses: List<ExpenseEntity>,
        goals: List<GoalEntity>,
        settings: List<AppSettingEntity>
    ): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "Pock-Em")
        root.put("exportedAt", System.currentTimeMillis())

        // Categories
        val catArray = JSONArray()
        for (c in categories) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("percentage", c.percentage)
                put("description", c.description)
                put("colorHex", c.colorHex)
                put("displayOrder", c.displayOrder)
            }
            catArray.put(obj)
        }
        root.put("categories", catArray)

        // Allocations
        val allocArray = JSONArray()
        for (a in allocations) {
            val obj = JSONObject().apply {
                put("id", a.id)
                put("timestamp", a.timestamp)
                put("totalAmount", a.totalAmount)
                put("note", a.note)
            }
            allocArray.put(obj)
        }
        root.put("allocations", allocArray)

        // Splits
        val splitArray = JSONArray()
        for (s in splits) {
            val obj = JSONObject().apply {
                put("id", s.id)
                put("allocationId", s.allocationId)
                put("categoryId", s.categoryId)
                put("categoryName", s.categoryName)
                put("amount", s.amount)
                put("percentage", s.percentage)
            }
            splitArray.put(obj)
        }
        root.put("splits", splitArray)

        // Expenses
        val expArray = JSONArray()
        for (e in expenses) {
            val obj = JSONObject().apply {
                put("id", e.id)
                put("categoryId", e.categoryId)
                put("categoryName", e.categoryName)
                put("amount", e.amount)
                put("description", e.description)
                put("timestamp", e.timestamp)
            }
            expArray.put(obj)
        }
        root.put("expenses", expArray)

        // Goals
        val goalArray = JSONArray()
        for (g in goals) {
            val obj = JSONObject().apply {
                put("id", g.id)
                put("title", g.title)
                put("targetAmount", g.targetAmount)
                put("linkedCategoryId", g.linkedCategoryId ?: JSONObject.NULL)
                put("manualCurrentAmount", g.manualCurrentAmount)
                put("createdAt", g.createdAt)
            }
            goalArray.put(obj)
        }
        root.put("goals", goalArray)

        // Settings
        val setArray = JSONArray()
        for (s in settings) {
            val obj = JSONObject().apply {
                put("key", s.key)
                put("value", s.value)
            }
            setArray.put(obj)
        }
        root.put("settings", setArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): BackupData {
        val root = JSONObject(jsonString)
        val version = root.optInt("version", 1)
        val appName = root.optString("appName", "Pock-Em")
        val exportedAt = root.optLong("exportedAt", System.currentTimeMillis())

        val categories = mutableListOf<CategoryEntity>()
        val catArray = root.optJSONArray("categories") ?: JSONArray()
        for (i in 0 until catArray.length()) {
            val obj = catArray.getJSONObject(i)
            categories.add(
                CategoryEntity(
                    id = obj.optLong("id", 0L),
                    name = obj.getString("name"),
                    percentage = obj.getDouble("percentage"),
                    description = obj.optString("description", ""),
                    colorHex = obj.optString("colorHex", "#10B981"),
                    displayOrder = obj.optInt("displayOrder", i)
                )
            )
        }

        val allocations = mutableListOf<AllocationEntity>()
        val allocArray = root.optJSONArray("allocations") ?: JSONArray()
        for (i in 0 until allocArray.length()) {
            val obj = allocArray.getJSONObject(i)
            allocations.add(
                AllocationEntity(
                    id = obj.optLong("id", 0L),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    totalAmount = obj.getDouble("totalAmount"),
                    note = obj.optString("note", "")
                )
            )
        }

        val splits = mutableListOf<AllocationSplitEntity>()
        val splitArray = root.optJSONArray("splits") ?: JSONArray()
        for (i in 0 until splitArray.length()) {
            val obj = splitArray.getJSONObject(i)
            splits.add(
                AllocationSplitEntity(
                    id = obj.optLong("id", 0L),
                    allocationId = obj.getLong("allocationId"),
                    categoryId = obj.getLong("categoryId"),
                    categoryName = obj.optString("categoryName", "Category"),
                    amount = obj.getDouble("amount"),
                    percentage = obj.optDouble("percentage", 0.0)
                )
            )
        }

        val expenses = mutableListOf<ExpenseEntity>()
        val expArray = root.optJSONArray("expenses") ?: JSONArray()
        for (i in 0 until expArray.length()) {
            val obj = expArray.getJSONObject(i)
            expenses.add(
                ExpenseEntity(
                    id = obj.optLong("id", 0L),
                    categoryId = obj.getLong("categoryId"),
                    categoryName = obj.optString("categoryName", "Expense"),
                    amount = obj.getDouble("amount"),
                    description = obj.optString("description", ""),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            )
        }

        val goals = mutableListOf<GoalEntity>()
        val goalArray = root.optJSONArray("goals") ?: JSONArray()
        for (i in 0 until goalArray.length()) {
            val obj = goalArray.getJSONObject(i)
            val linkedCategoryId = if (obj.has("linkedCategoryId") && !obj.isNull("linkedCategoryId")) obj.getLong("linkedCategoryId") else null
            val manualCurrentAmount = obj.optDouble("manualCurrentAmount", 0.0)
            goals.add(
                GoalEntity(
                    id = obj.optLong("id", 0L),
                    title = obj.getString("title"),
                    targetAmount = obj.getDouble("targetAmount"),
                    linkedCategoryId = linkedCategoryId,
                    manualCurrentAmount = manualCurrentAmount,
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                )
            )
        }

        val settings = mutableListOf<AppSettingEntity>()
        val setArray = root.optJSONArray("settings") ?: JSONArray()
        for (i in 0 until setArray.length()) {
            val obj = setArray.getJSONObject(i)
            settings.add(
                AppSettingEntity(
                    key = obj.getString("key"),
                    value = obj.getString("value")
                )
            )
        }

        return BackupData(
            version = version,
            appName = appName,
            exportedAt = exportedAt,
            categories = categories,
            allocations = allocations,
            splits = splits,
            expenses = expenses,
            goals = goals,
            settings = settings
        )
    }
}
