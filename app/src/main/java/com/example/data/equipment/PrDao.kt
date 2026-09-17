package com.example.data.equipment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PrDao {
    @Query("SELECT * FROM pr_requests ORDER BY id DESC")
    fun getAllPrRequests(): Flow<List<PrRequest>>

    @Query("SELECT * FROM pr_requests WHERE status = 'Pending' ORDER BY id DESC")
    fun getPendingPrRequests(): Flow<List<PrRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrRequest(request: PrRequest): Long

    @Update
    suspend fun updatePrRequest(request: PrRequest)

    @Query("UPDATE pr_requests SET status = :status, remarks = :remarks, reviewedBy = :adminId, reviewedAt = :timestamp WHERE id = :id")
    suspend fun updatePrStatus(id: Long, status: String, remarks: String, adminId: String, timestamp: String)

    @Query("DELETE FROM pr_requests WHERE id = :id")
    suspend fun deletePrRequest(id: Long)
}
