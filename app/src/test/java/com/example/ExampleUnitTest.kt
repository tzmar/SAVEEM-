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

    private fun calculateSplits(amount: Double, categories: List<CategoryEntity>): List<Double> {
        val totalCents = (amount * 100.0).roundToLong()
        var remainingCents = totalCents
        val splits = mutableListOf<Double>()

        for (i in categories.indices) {
            val cat = categories[i]
            val splitCents = if (i == categories.lastIndex) {
                remainingCents
            } else {
                val calculated = ((totalCents * cat.percentage) / 100.0).roundToLong()
                calculated.coerceAtMost(remainingCents)
            }
            remainingCents -= splitCents
            splits.add(splitCents / 100.0)
        }
        return splits
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
}
