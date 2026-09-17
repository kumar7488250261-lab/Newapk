package com.example.data.equipment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LongHourDao {
    @Query("SELECT * FROM long_hour_records ORDER BY isClosed ASC, id DESC")
    fun getAllRecords(): Flow<List<LongHourDutyRecord>>

    @Query("SELECT * FROM long_hour_records WHERE isClosed = 0 ORDER BY id DESC")
    fun getActiveRecords(): Flow<List<LongHourDutyRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: LongHourDutyRecord): Long

    @Update
    suspend fun updateRecord(record: LongHourDutyRecord)

    @Query("""
        UPDATE long_hour_records 
        SET isClosed = 1, 
            reliefDate = :reliefDate, 
            reliefTime = :reliefTime, 
            reliefStationCode = :reliefStationCode,
            closedBy = :closedBy,
            closedAt = :closedAt
        WHERE id = :id
    """)
    suspend fun closeDuty(
        id: Long,
        reliefDate: String,
        reliefTime: String,
        reliefStationCode: String,
        closedBy: String,
        closedAt: String
    )

    @Query("DELETE FROM long_hour_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)
}
