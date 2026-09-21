package com.example.data.equipment

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "roster_tlc_records")
data class RosterTlcRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rosterDate: String, // dd-MM-yyyy
    val shiftTiming: String, // "06-14", "14-22", "22-06"
    // Roles with Auto-fetch (name + mobile)
    val tfrCrewName: String = "",
    val tfrMobile: String = "",
    val lhCrewName: String = "",
    val lhMobile: String = "",
    val diCrewName: String = "",
    val diMobile: String = "",
    val wdCrewName: String = "",
    val wdMobile: String = "",
    // Manual fill
    val cmsName: String = "",
    // Lobby CLI fetch with name and mobile
    val lobbyCliShift: String = "00-08", // "00-08", "08-16", "16-00"
    val lobbyCliName: String = "",
    val lobbyCliMobile: String = "",
    // Sander manual fill
    val sanderBoyShift: String = "00-08", // "00-08", "08-16", "16-00"
    val sanderBoyName: String = "",
    // TLC details
    val tlcShift: String = "17-01", // "17-01", "01-09", "09-17"
    val tlcMlName: String = "",
    val tlcMlMobile: String = "",
    val tlcLhName: String = "",
    val tlcLhMobile: String = "",
    val remarks: String = "",
    val updatedByAdmin: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface RosterTlcDao {
    @Query("SELECT * FROM roster_tlc_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<RosterTlcRecord>>

    @Query("SELECT * FROM roster_tlc_records WHERE rosterDate = :date ORDER BY id DESC")
    fun getRecordsByDate(date: String): Flow<List<RosterTlcRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RosterTlcRecord): Long

    @Query("DELETE FROM roster_tlc_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)
}

@JsonClass(generateAdapter = true)
data class RosterTlcSheetPayload(
    val action: String = "ROSTER_TLC_UPDATE",
    val id: Long,
    val rosterDate: String,
    val shiftTiming: String,
    val tfrCrewName: String,
    val tfrMobile: String,
    val lhCrewName: String,
    val lhMobile: String,
    val diCrewName: String,
    val diMobile: String,
    val wdCrewName: String,
    val wdMobile: String,
    val cmsName: String,
    val lobbyCliShift: String,
    val lobbyCliName: String,
    val lobbyCliMobile: String,
    val sanderBoyShift: String,
    val sanderBoyName: String,
    val tlcShift: String = "",
    val tlcMlName: String,
    val tlcMlMobile: String,
    val tlcLhName: String,
    val tlcLhMobile: String,
    val remarks: String,
    val updatedByAdmin: String,
    val timestamp: String
)

@JsonClass(generateAdapter = true)
data class JeepMovementSheetPayload(
    val action: String = "JEEP_MOVEMENT_UPDATE",
    val id: Long,
    val jeepNo: String,
    val driverName: String,
    val toTime: String,
    val fromStation: String,
    val departureDate: String,
    val departureTime: String,
    val toStation: String,
    val arrivalDate: String,
    val arrivalTime: String,
    val reliefTime: String,
    val outwardCrews: String,
    val returningCrews: String,
    val returningFromStation: String,
    val returningDepartureDate: String,
    val returningDepartureTime: String,
    val returningToStation: String,
    val returningArrivalDate: String,
    val returningArrivalTime: String,
    val isCompleted: Boolean,
    val timestamp: String
)

@JsonClass(generateAdapter = true)
data class PrRemarkSheetPayload(
    val action: String = "PR_REMARK_UPDATE",
    val id: Long,
    val crewId: String,
    val crewName: String,
    val designation: String,
    val signOffDate: String,
    val signOffTime: String,
    val requestDate: String,
    val status: String,
    val remarks: String,
    val adminId: String?,
    val reviewedTimestamp: String?,
    val timestamp: String
)
