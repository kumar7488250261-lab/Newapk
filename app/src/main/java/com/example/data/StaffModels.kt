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
    val lobbies: List<Lobby>
)
