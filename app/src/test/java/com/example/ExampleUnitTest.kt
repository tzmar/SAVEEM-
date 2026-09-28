package com.example

import com.example.data.model.CategoryEntity
import kotlin.math.roundToLong
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    private val defaultCategories = listOf(
        CategoryEntity(id = 1, name = "Future / Zimbabwe Fund", percentage = 40.0),
        CategoryEntity(id = 2, name = "Tools & Skills", percentage = 30.0),
        CategoryEntity(id = 3, name = "Personal / Fun", percentage = 20.0),
        CategoryEntity(id = 4, name = "Family / Miscellaneous", percentage = 10.0)
    )

    private fun calculateSplits(
        amount: Double,
        categoriesList: List<CategoryEntity>,
        roundingCategoryId: Long? = null
    ): List<Double> {
        if (amount <= 0.0 || categoriesList.isEmpty()) return emptyList()

        val cleanAmount = Math.round(amount * 100.0) / 100.0
        if (cleanAmount <= 0.0) return emptyList()

        val roundingCat = (if (roundingCategoryId != null) {
            categoriesList.find { it.id == roundingCategoryId }
        } else null) ?: categoriesList.find {
            it.name.contains("Personal", ignoreCase = true) ||
            it.name.contains("Fun", ignoreCase = true)
        } ?: categoriesList.last()

        val allocations = mutableMapOf<Long, Double>()
        var sumOthers = 0L

        for (cat in categoriesList) {
            if (cat.id != roundingCat.id) {
                val raw = (cleanAmount * cat.percentage) / 100.0
                val floored = Math.floor(raw).toLong().coerceAtLeast(0L)
                allocations[cat.id] = floored.toDouble()
                sumOthers += floored
            }
        }

        val roundingCatAmount = Math.round((cleanAmount - sumOthers) * 100.0) / 100.0
        allocations[roundingCat.id] = roundingCatAmount.coerceAtLeast(0.0)

        return categoriesList.map { cat ->
            allocations[cat.id] ?: 0.0
        }
    }

    @Test
    fun testAllocation_P100() {
        // For P100:
        // Future / Zimbabwe = P40
        // Tools & Skills = P30
        // Personal / Fun = P20
        // Family / Miscellaneous = P10
        val splits = calculateSplits(100.0, defaultCategories)
        assertEquals(40.0, splits[0], 0.001)
        assertEquals(30.0, splits[1], 0.001)
        assertEquals(20.0, splits[2], 0.001)
        assertEquals(10.0, splits[3], 0.001)
    }

    @Test
    fun testAllocation_P1000() {
        // For P1,000:
        // Future / Zimbabwe = P400
        // Tools & Skills = P300
        // Personal / Fun = P200
        // Family / Miscellaneous = P100
        val splits = calculateSplits(1000.0, defaultCategories)
        assertEquals(400.0, splits[0], 0.001)
        assertEquals(300.0, splits[1], 0.001)
        assertEquals(200.0, splits[2], 0.001)
        assertEquals(100.0, splits[3], 0.001)
    }

    @Test
    fun testAllocation_P3000() {
        // For P3,000:
        // Future / Zimbabwe = P1,200
        // Tools & Skills = P900
        // Personal / Fun = P600
        // Family / Miscellaneous = P300
        val splits = calculateSplits(3000.0, defaultCategories)
        assertEquals(1200.0, splits[0], 0.001)
        assertEquals(900.0, splits[1], 0.001)
        assertEquals(600.0, splits[2], 0.001)
        assertEquals(300.0, splits[3], 0.001)
    }

    @Test
    fun testAllocation_SmallAmount_P20() {
        val splits = calculateSplits(20.0, defaultCategories)
        assertEquals(8.0, splits[0], 0.001)
        assertEquals(6.0, splits[1], 0.001)
        assertEquals(4.0, splits[2], 0.001)
        assertEquals(2.0, splits[3], 0.001)
    }

    private fun recalculateRemainingPercentages(categories: List<CategoryEntity>): List<CategoryEntity> {
        if (categories.isEmpty()) return emptyList()
        if (categories.size == 1) {
            return listOf(categories[0].copy(percentage = 100.0, displayOrder = 0))
        }

        val totalRemaining = categories.sumOf { it.percentage }
        var sumSoFar = 0.0

        return categories.mapIndexed { index, cat ->
            val newPct = if (index == categories.lastIndex) {
                val remaining = 100.0 - sumSoFar
                Math.round(remaining * 10.0) / 10.0
            } else {
                val proportional = if (totalRemaining > 0.0) {
                    (cat.percentage / totalRemaining) * 100.0
                } else {
                    100.0 / categories.size
                }
                val rounded = Math.round(proportional * 10.0) / 10.0
                sumSoFar += rounded
                rounded
            }
            cat.copy(percentage = newPct.coerceAtLeast(0.0), displayOrder = index)
        }
    }

    @Test
    fun testCategoryDeletion_RedistributeProportionally_UserExample() {
        // Initial: 40% + 30% + 20% + 10% = 100%
        val initial = listOf(
            CategoryEntity(id = 1, name = "Cat A", percentage = 40.0),
            CategoryEntity(id = 2, name = "Cat B", percentage = 30.0),
            CategoryEntity(id = 3, name = "Cat C", percentage = 20.0),
            CategoryEntity(id = 4, name = "Cat D", percentage = 10.0)
        )

        // Delete 10% category (Cat D)
        val remaining = initial.filter { it.id != 4L }
        val recalculated = recalculateRemainingPercentages(remaining)

        assertEquals(3, recalculated.size)
        assertEquals(44.4, recalculated[0].percentage, 0.001)
        assertEquals(33.3, recalculated[1].percentage, 0.001)
        assertEquals(22.3, recalculated[2].percentage, 0.001)

        val total = recalculated.sumOf { it.percentage }
        assertEquals(100.0, total, 0.001)
    }

    @Test
    fun testCategoryDeletion_EqualHalves() {
        val initial = listOf(
            CategoryEntity(id = 1, name = "Cat A", percentage = 50.0),
            CategoryEntity(id = 2, name = "Cat B", percentage = 25.0),
            CategoryEntity(id = 3, name = "Cat C", percentage = 25.0)
        )

        // Delete Cat A (50%)
        val remaining = initial.filter { it.id != 1L }
        val recalculated = recalculateRemainingPercentages(remaining)

        assertEquals(2, recalculated.size)
        assertEquals(50.0, recalculated[0].percentage, 0.001)
        assertEquals(50.0, recalculated[1].percentage, 0.001)

        val total = recalculated.sumOf { it.percentage }
        assertEquals(100.0, total, 0.001)
    }

    @Test
    fun testCategoryDeletion_SingleRemainingGets100() {
        val initial = listOf(
            CategoryEntity(id = 1, name = "Cat A", percentage = 70.0),
            CategoryEntity(id = 2, name = "Cat B", percentage = 30.0)
        )

        // Delete Cat B (30%)
        val remaining = initial.filter { it.id != 2L }
        val recalculated = recalculateRemainingPercentages(remaining)

        assertEquals(1, recalculated.size)
        assertEquals(100.0, recalculated[0].percentage, 0.001)
        assertEquals(100.0, recalculated.sumOf { it.percentage }, 0.001)
    }

    @Test
    fun testAllocation_PersonalFunAbsorbsRounding_UserExample() {
        // Zimbabwe: 40.2%, Internet: 10.1%, Tools: 20.4%, Personal / Fun: 29.3%
        val cats = listOf(
            CategoryEntity(id = 1, name = "Zimbabwe", percentage = 40.2),
            CategoryEntity(id = 2, name = "Internet", percentage = 10.1),
            CategoryEntity(id = 3, name = "Tools", percentage = 20.4),
            CategoryEntity(id = 4, name = "Personal / Fun", percentage = 29.3)
        )

        // Amount = 100
        val splits = calculateSplits(100.0, cats)

        // Zimbabwe = P40.20 -> P40
        // Internet = P10.10 -> P10
        // Tools = P20.40 -> P20
        // Personal / Fun receives all the rounding differences -> P30
        assertEquals(40.0, splits[0], 0.001)
        assertEquals(10.0, splits[1], 0.001)
        assertEquals(20.0, splits[2], 0.001)
        assertEquals(30.0, splits[3], 0.001)

        assertEquals(100.0, splits.sum(), 0.001)
    }

    @Test
    fun testAllocation_DecimalPointFiveRoundedDown_UserSpecification() {
        // If a category produces P20.50, round it down to P20 and move the P0.50 difference to Personal / Fun
        val cats = listOf(
            CategoryEntity(id = 1, name = "Tools", percentage = 41.0),
            CategoryEntity(id = 2, name = "Personal / Fun", percentage = 59.0)
        )

        // Amount = 50. Tools raw = 50 * 0.41 = 20.50 -> rounds down to 20.0
        // Personal / Fun raw = 29.50 -> receives 50 - 20 = 30.0
        val splits = calculateSplits(50.0, cats)

        assertEquals(20.0, splits[0], 0.001)
        assertEquals(30.0, splits[1], 0.001)
        assertEquals(50.0, splits.sum(), 0.001)
    }

    @Test
    fun testAllocation_WholePula_UserFiveCategoryP50Example() {
        // Future: 36%, Tools: 27%, Personal/Fun: 18%, Family: 9%, Data: 10%
        val cats = listOf(
            CategoryEntity(id = 1, name = "Future / Zimbabwe", percentage = 36.0),
            CategoryEntity(id = 2, name = "Tools & Skills", percentage = 27.0),
            CategoryEntity(id = 3, name = "Personal / Fun", percentage = 18.0),
            CategoryEntity(id = 4, name = "Family", percentage = 9.0),
            CategoryEntity(id = 5, name = "Data", percentage = 10.0)
        )

        val splits = calculateSplits(50.0, cats)

        // All amounts must be clean whole Pula (no .50 or .45 or decimals)
        for (amount in splits) {
            assertEquals("Amount must be whole Pula", 0.0, amount % 1.0, 0.001)
        }

        // Expected with floor:
        // Future (36% of 50 = 18.0) -> 18.0
        // Tools (27% of 50 = 13.5) -> 13.0 (floored)
        // Family (9% of 50 = 4.5) -> 4.0 (floored)
        // Data (10% of 50 = 5.0) -> 5.0
        // Personal/Fun receives leftover: 50 - (18 + 13 + 4 + 5) = 10.0
        assertEquals(18.0, splits[0], 0.001)
        assertEquals(13.0, splits[1], 0.001)
        assertEquals(10.0, splits[2], 0.001)
        assertEquals(4.0, splits[3], 0.001)
        assertEquals(5.0, splits[4], 0.001)

        // Sum must be EXACTLY 50.0
        assertEquals(50.0, splits.sum(), 0.001)
    }

    @Test
    fun testAllocation_WholePula_AddingInternetCategory() {
        val cats = listOf(
            CategoryEntity(id = 1, name = "Future / Zimbabwe", percentage = 34.0),
            CategoryEntity(id = 2, name = "Tools & Skills", percentage = 26.0),
            CategoryEntity(id = 3, name = "Personal / Fun", percentage = 17.0),
            CategoryEntity(id = 4, name = "Family", percentage = 9.0),
            CategoryEntity(id = 5, name = "Data", percentage = 9.0),
            CategoryEntity(id = 6, name = "Internet", percentage = 5.0)
        )

        val splits = calculateSplits(250.0, cats)

        for (amount in splits) {
            assertEquals("Amount must be whole Pula", 0.0, amount % 1.0, 0.001)
        }

        assertEquals(250.0, splits.sum(), 0.001)
    }

    @Test
    fun testAllocation_EnteredAmountWithDecimals_UserP160_30Example() {
        // User example:
        // Entered amount = P160.30
        // Zimbabwe = P63
        // Tools = P46
        // Personal / Fun = P30.30
        // Family = P15
        // Internet = P6
        // Total = exactly P160.30
        val cats = listOf(
            CategoryEntity(id = 1, name = "Zimbabwe", percentage = 39.5),
            CategoryEntity(id = 2, name = "Tools", percentage = 28.8),
            CategoryEntity(id = 3, name = "Personal / Fun", percentage = 18.2),
            CategoryEntity(id = 4, name = "Family", percentage = 9.5),
            CategoryEntity(id = 5, name = "Internet", percentage = 4.0)
        )

        val splits = calculateSplits(160.30, cats)

        // Zimbabwe = floor(160.30 * 0.395) = floor(63.3185) = 63.0
        // Tools = floor(160.30 * 0.288) = floor(46.1664) = 46.0
        // Family = floor(160.30 * 0.095) = floor(15.2285) = 15.0
        // Internet = floor(160.30 * 0.040) = floor(6.412) = 6.0
        // Personal / Fun = 160.30 - (63 + 46 + 15 + 6) = 30.30
        assertEquals(63.0, splits[0], 0.001)
        assertEquals(46.0, splits[1], 0.001)
        assertEquals(30.30, splits[2], 0.001)
        assertEquals(15.0, splits[3], 0.001)
        assertEquals(6.0, splits[4], 0.001)

        // Sum must be EXACTLY 160.30
        assertEquals(160.30, splits.sum(), 0.001)
    }

    @Test
    fun testAllocation_SwitchRoundingDestination_UpdatesSuccessfully() {
        val cats = listOf(
            CategoryEntity(id = 1, name = "Zimbabwe", percentage = 40.0),
            CategoryEntity(id = 2, name = "Tools", percentage = 30.0),
            CategoryEntity(id = 3, name = "Personal / Fun", percentage = 20.0),
            CategoryEntity(id = 4, name = "Internet", percentage = 10.0)
        )

        // Case A: Default (null roundingCategoryId) -> goes to Personal / Fun (id = 3)
        val defaultSplits = calculateSplits(100.50, cats, roundingCategoryId = null)
        assertEquals(40.0, defaultSplits[0], 0.001) // Zimbabwe
        assertEquals(30.0, defaultSplits[1], 0.001) // Tools
        assertEquals(20.50, defaultSplits[2], 0.001) // Personal / Fun receives .50
        assertEquals(10.0, defaultSplits[3], 0.001) // Internet
        assertEquals(100.50, defaultSplits.sum(), 0.001)

        // Case B: User switches "Rounding goes to" -> Tools (id = 2)
        val toolsSplits = calculateSplits(100.50, cats, roundingCategoryId = 2L)
        assertEquals(40.0, toolsSplits[0], 0.001) // Zimbabwe
        assertEquals(30.50, toolsSplits[1], 0.001) // Tools receives .50
        assertEquals(20.0, toolsSplits[2], 0.001) // Personal / Fun is whole 20.0
        assertEquals(10.0, toolsSplits[3], 0.001) // Internet
        assertEquals(100.50, toolsSplits.sum(), 0.001)

        // Case C: User switches "Rounding goes to" -> Zimbabwe (id = 1)
        val zimSplits = calculateSplits(100.50, cats, roundingCategoryId = 1L)
        assertEquals(40.50, zimSplits[0], 0.001) // Zimbabwe receives .50
        assertEquals(30.0, zimSplits[1], 0.001) // Tools
        assertEquals(20.0, zimSplits[2], 0.001) // Personal / Fun
        assertEquals(10.0, zimSplits[3], 0.001) // Internet
        assertEquals(100.50, zimSplits.sum(), 0.001)
    }

    @Test
    fun testBackupManager_CreateAndParseJson() {
        val cats = listOf(
            CategoryEntity(id = 1, name = "Zimbabwe", percentage = 40.0),
            CategoryEntity(id = 2, name = "Tools", percentage = 30.0)
        )
        val allocs = listOf(
            com.example.data.model.AllocationEntity(id = 10, timestamp = 1000L, totalAmount = 150.0, note = "Test Note")
        )
        val splits = listOf(
            com.example.data.model.AllocationSplitEntity(id = 100, allocationId = 10, categoryId = 1, categoryName = "Zimbabwe", amount = 60.0, percentage = 40.0)
        )
        val expenses = listOf(
            com.example.data.model.ExpenseEntity(id = 50, categoryId = 2, categoryName = "Tools", amount = 25.0, description = "Hammer")
        )
        val goals = listOf(
            com.example.data.model.GoalEntity(id = 5, title = "New Laptop", targetAmount = 5000.0)
        )
        val settings = listOf(
            com.example.data.model.AppSettingEntity(key = "currency_symbol", value = "P")
        )

        val json = com.example.util.BackupManager.createBackupJson(cats, allocs, splits, expenses, goals, settings)
        assertTrue(json.contains("Pock-Em"))
        assertTrue(json.contains("Zimbabwe"))
        assertTrue(json.contains("Hammer"))

        val parsed = com.example.util.BackupManager.parseBackupJson(json)
        assertEquals("Pock-Em", parsed.appName)
        assertEquals(2, parsed.categories.size)
        assertEquals(1, parsed.allocations.size)
        assertEquals(1, parsed.splits.size)
        assertEquals(1, parsed.expenses.size)
        assertEquals(1, parsed.goals.size)
        assertEquals(1, parsed.settings.size)
        assertEquals("Zimbabwe", parsed.categories[0].name)
        assertEquals(150.0, parsed.allocations[0].totalAmount, 0.001)
        assertEquals("Hammer", parsed.expenses[0].description)
        assertEquals("New Laptop", parsed.goals[0].title)
        assertEquals("P", parsed.settings[0].value)
    }
}
