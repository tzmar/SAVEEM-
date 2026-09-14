package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class AllocationWithSplits(
    @Embedded val allocation: AllocationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "allocationId"
    )
    val splits: List<AllocationSplitEntity>
)
