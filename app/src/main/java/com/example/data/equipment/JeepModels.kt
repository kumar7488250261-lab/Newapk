package com.example.data.equipment

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

object JeepConstants {
    val JEEP_LIST = listOf(
        "89",
        "89(ll)",
        "91",
        "22",
        "31",
        "79",
        "Breakdown"
    )

    val CORE_SIX_JEEPS = listOf(
        "89",
        "89(ll)",
        "91",
        "22",
        "31",
        "79"
    )

    data class Station(val code: String, val name: String) {
        val displayLabel: String get() = "$code - $name"
    }

    val STATIONS = listOf(
        Station("KHS", "Kharsia"),
        Station("JDI", "Jharadih"),
        Station("SKT", "Sakti"),
        Station("BUA", "Baradwar"),
        Station("ROB", "Robertson"),
        Station("BEF", "Bhupdevpur"),
        Station("VWLR", "VWLR"),
        Station("MONET", "Monet"),
        Station("VIMLA", "Vimla"),
        Station("BEMR", "BEMR"),
        Station("GURA", "Gurda"),
        Station("CHHL", "Chaal"),
        Station("SLCC", "Chaal Silo"),
        Station("GGDA", "Gharghoda"),
        Station("KCHP", "Karichapar"),
        Station("BOMK", "Bomka"),
        Station("BAROD", "Barod"),
        Station("DMJG", "Dharmjaygarh"),
        Station("BUMA", "Bhalumuda"),
        Station("RIG", "Raigarh"),
        Station("BSP", "Bilaspur"),
        Station("KDTR", "Kirodimal"),
        Station("SGRD", "Saragaon"),
        Station("OTHER", "Other")
    )
}

@JsonClass(generateAdapter = true)
data class CrewSlotItem(
    val slotIndex: Int, // 0 to 6
    val type: String = "EMPTY", // "EMPTY", "CREW", "OTHER"
    val crewId: String = "",
    val crewName: String = "",
    val designation: String = "",
    val otherText: String = ""
) {
    fun getDisplaySummary(): String {
        return when (type) {
            "EMPTY" -> "Empty (खाली गया)"
            "OTHER" -> if (otherText.isNotBlank()) "Other: $otherText" else "Other"
            else -> if (crewName.isNotBlank()) "$crewName (${designation.ifBlank { crewId }})" else "Empty"
        }
    }
}

@Entity(tableName = "jeep_movements")
data class JeepMovementRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jeepNo: String,
    val driverName: String,
    
    // Outward Journey Details
    val toTime: String = "",
    val fromStation: String = "KHS",
    val departureDate: String = "",
    val departureTime: String = "",
    val toStation: String = "",
    val arrivalDate: String = "",
    val arrivalTime: String = "",
    val reliefTime: String = "",
    val outwardCrews: String = "", // Formatted readable summary or JSON
    
    // Returning Journey Details
    val returningCrews: String = "",
    val returningFromStation: String = "",
    val returningDepartureDate: String = "",
    val returningDepartureTime: String = "",
    val returningToStation: String = "KHS",
    val returningArrivalDate: String = "",
    val returningArrivalTime: String = "",
    
    val isCompleted: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class JeepAvailabilityItem(
    val jeepNo: String,
    val isAvailable: Boolean,
    val turnNumber: Int, // 1, 2, 3... based on FIFO arrival order
    val lastArrivalDate: String,
    val lastArrivalTime: String,
    val arrivalTimestamp: Long,
    val driverName: String,
    val currentLocation: String,
    val movementDestination: String = "",
    val departureTime: String = "",
    val departureDate: String = "",
    val recentCrewCount: Int = 0,
    val crewSummary: String = "",
    val statusDescription: String = ""
)
