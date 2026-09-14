package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val percentage: Double,
    val description: String = "",
    val colorHex: String = "#10B981",
    val displayOrder: Int = 0
)
