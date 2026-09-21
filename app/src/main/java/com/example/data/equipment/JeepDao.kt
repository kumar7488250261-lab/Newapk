package com.example.data.equipment

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JeepDao {

    @Query("SELECT * FROM jeep_movements ORDER BY timestamp DESC")
    fun getAllMovements(): Flow<List<JeepMovementRecord>>

    @Query("SELECT * FROM jeep_movements WHERE jeepNo = :jeepNo ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestMovementForJeep(jeepNo: String): JeepMovementRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(record: JeepMovementRecord): Long

    @Update
    suspend fun updateMovement(record: JeepMovementRecord)

    @Query("DELETE FROM jeep_movements WHERE id = :id")
    suspend fun deleteMovement(id: Long)
}
