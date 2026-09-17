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
