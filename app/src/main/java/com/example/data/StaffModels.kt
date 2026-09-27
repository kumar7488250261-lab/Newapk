package com.example.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StaffContact(
    val name: String,
    val designation: String,
    val mobile: String,
    val cug: String = ""
)

@JsonClass(generateAdapter = true)
data class LobbyCategory(
    val category: String,
    val contacts: List<StaffContact>
)

@JsonClass(generateAdapter = true)
data class Lobby(
    val id: Int,
    val code: String,
    val name: String,
    val categories: List<LobbyCategory>
) {
    val totalContacts: Int
        get() = categories.sumOf { it.contacts.size }
}

@JsonClass(generateAdapter = true)
data class DirectoryResponse(
    val lobbies: List<Lobby> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CrewMember(
    val crewId: String = "",
    val name: String = "",
    val designation: String = "",
    val category: String = "",
    val cadre: String = "",
    val mobile: String = ""
)

@JsonClass(generateAdapter = true)
data class StationContact(
    val sNo: Int = 0,
    val code: String = "",
    val name: String = "",
    val cugMobile: String = "",
    val landline: String = "",
    val section: String = ""
)

@JsonClass(generateAdapter = true)
data class StationResponse(
    val stations: List<StationContact> = emptyList()
)
