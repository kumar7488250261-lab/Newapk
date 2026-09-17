package com.example.data.equipment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipment_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<EquipmentRecord>>

    @Query("SELECT * FROM equipment_records WHERE status = 'ISSUED' ORDER BY id DESC")
    fun getActiveIssuedRecords(): Flow<List<EquipmentRecord>>

    @Query("SELECT COUNT(*) FROM equipment_records WHERE status = 'ISSUED'")
    fun getActiveIssuedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: EquipmentRecord): Long

    @Update
    suspend fun updateRecord(record: EquipmentRecord)

    @Query("DELETE FROM equipment_records WHERE id = :recordId")
    suspend fun deleteRecord(recordId: Long)

    @Query("SELECT * FROM equipment_records WHERE status = 'ISSUED' AND (issuedToCrewId LIKE '%' || :query || '%' OR issuedToCrewName LIKE '%' || :query || '%' OR equipmentName LIKE '%' || :query || '%')")
    fun searchIssuedRecords(query: String): Flow<List<EquipmentRecord>>
}
