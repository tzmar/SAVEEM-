package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "allocation_splits",
    foreignKeys = [
        ForeignKey(
            entity = AllocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["allocationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("allocationId"), Index("categoryId")]
)
data class AllocationSplitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val allocationId: Long,
    val categoryId: Long,
    val categoryName: String,
    val amount: Double,
    val percentage: Double
)
