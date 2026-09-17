package com.example.data.equipment

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class EquipmentStatus {
    ISSUED,
    RETURNED
}

enum class DesignationCategory {
    LP,
    ALP,
    GUARD
}

@JsonClass(generateAdapter = true)
data class CrewMember(
    val crewId: String,
    val name: String,
    val designation: String,
    val category: String,
    val mobile: String = "",
    val cmsId: String = ""
)

@Entity(tableName = "equipment_records")
data class EquipmentRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val equipmentName: String,
    val equipmentSerialNo: String,
    val issuedToCrewId: String,
    val issuedToCrewName: String,
    val designation: String,
    val category: DesignationCategory,
    val issueTime: String,
    val returnTime: String? = null,
    val status: EquipmentStatus = EquipmentStatus.ISSUED,
    val trainNo: String = "",
    val fromStation: String = "KHS",
    val toStation: String = "",
    val remarks: String = "",
    val isSynced: Boolean = false
)

@Entity(tableName = "pr_requests")
data class PrRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val crewId: String,
    val crewName: String,
    val designation: String,
    val signOffDate: String,
    val signOffTime: String,
    val requestDate: String,
    val status: String = "Pending", // "Pending", "Confirmed", "Not Due"
    val remarks: String = "", // Admin remarks: e.g. "PR updated", "PR not Due"
    val reviewedBy: String? = null,
    val reviewedAt: String? = null
)

@Entity(tableName = "long_hour_records")
data class LongHourDutyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lpgId: String,
    val lpgName: String,
    val alpId: String,
    val alpName: String,
    val trainNo: String,
    val locoNo: String,
    val signOnDate: String,      // e.g. "17-09-2026"
    val signOnTime: String,      // e.g. "08:30"
    val direction: String,       // "UP", "DN", "CIC"
    val currentStationCode: String,
    val arrivalTimeCurrentStation: String,
    val currentTrainPosition: String, // 1 to 7
    val positionTiming: String,
    val isClosed: Boolean = false, // Active duty until admin closes
    val reliefDate: String? = null,
    val reliefTime: String? = null,
    val reliefStationCode: String? = null,
    val closedBy: String? = null,
    val closedAt: String? = null,
    val createdAt: String = "",
    val isSyncedToSheets: Boolean = false
)

