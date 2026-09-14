package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.AllocationEntity
import com.example.data.model.AllocationSplitEntity
import com.example.data.model.AllocationWithSplits
import kotlinx.coroutines.flow.Flow

@Dao
interface AllocationDao {
    @Transaction
    @Query("SELECT * FROM allocations ORDER BY timestamp DESC")
    fun getAllAllocationsWithSplits(): Flow<List<AllocationWithSplits>>

    @Query("SELECT * FROM allocation_splits")
    fun getAllSplits(): Flow<List<AllocationSplitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocation(allocation: AllocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplits(splits: List<AllocationSplitEntity>)

    @Query("DELETE FROM allocations WHERE id = :id")
    suspend fun deleteAllocationById(id: Long)

    @Query("DELETE FROM allocations")
    suspend fun deleteAllAllocations()

    @Query("DELETE FROM allocation_splits")
    suspend fun deleteAllSplits()
}
