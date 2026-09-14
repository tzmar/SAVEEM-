package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val linkedCategoryId: Long? = null,
    val manualCurrentAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
