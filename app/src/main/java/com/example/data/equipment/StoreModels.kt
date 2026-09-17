package com.example.data.equipment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_issue_records")
data class StoreIssueRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val issueDate: String,                 // e.g. "17-09-2026"
    val crewId: String,                    // e.g. "KHS1001"
    val crewName: String,                  // Auto fetched
    val designation: String,               // Auto fetched: LPG / Train Manager / ALP
    val roleType: String,                  // "LPG", "TRAIN_MANAGER", "ALP", "OTHER"
    val toTime: String,                    // Train Ordering Time e.g. "08:30"
    val toBooked: String,                  // Manually filled destination/train info

    // LPG items (Walkie Talkie, Spare Battery, Detonator)
    val walkieTalkieBrand: String? = null, // "Motorola", "Convey", "Other"
    val walkieTalkieNo: String? = null,
    val spareBatteryNo: String? = null,
    val detonatorNo: String? = null,

    // ALP items (FSD)
    val fsdBrand: String? = null,          // "Actech", "APAUL", "Other"
    val fsdNo: String? = null,

    // Common
    val notes: String = "",                // टिप्पणी या notes
    val status: String = "ISSUED_PENDING_APPROVAL", // "ISSUED_PENDING_APPROVAL", "APPROVED", "RETURNED_PENDING_APPROVAL", "RETURN_APPROVED"
    val approvedByAdminId: String? = null,
    val approvedByAdminName: String? = null,
    val approvedAt: String? = null,
    val issueShift: String = "",           // "06:00 - 14:00", "14:00 - 22:00", "22:00 - 06:00"

    // Return (CHO - Charge Hand Over)
    val isReturned: Boolean = false,
    val choDate: String? = null,
    val choTime: String? = null,
    val returnNotes: String? = null,
    val returnApprovedByAdminId: String? = null,
    val returnApprovedByAdminName: String? = null,
    val returnApprovedAt: String? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val isSyncedToSheets: Boolean = false
)

@Entity(tableName = "store_shift_records")
data class StoreShiftRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shiftDate: String,                 // "17-09-2026"
    val shiftSlot: String,                 // "06:00 - 14:00", "14:00 - 22:00", "22:00 - 06:00"
    val adminCrewId: String,               // Staff ID e.g. "KHS1001"
    val adminName: String,                 // Auto fetched
    val adminDesignation: String,          // Auto fetched
    val loginTime: String,
    val logoutTime: String? = null,
    val totalWalkieTalkieIssued: Int = 0,
    val totalBatteryIssued: Int = 0,
    val totalDetonatorIssued: Int = 0,
    val totalFsdIssued: Int = 0,
    val totalReturnsReceived: Int = 0,
    val isSyncedToSheets: Boolean = false
)
