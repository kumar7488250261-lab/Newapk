package com.example.data

data class StaffContact(
    val name: String,
    val mobile: String,
    val category: String,
    val lobbyCode: String,
    val lobbyName: String
)

data class LobbyCategory(
    val name: String,
    val fullName: String,
    val contacts: List<StaffContact>
)

data class Lobby(
    val id: String,
    val name: String,
    val code: String,
    val categories: List<LobbyCategory>,
    val totalContacts: Int
)
