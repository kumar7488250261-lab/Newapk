package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CrewContact(
    val name: String,
    val designation: String,
    val mobile: String,
    val cug: String = "",
    val category: String = "",
    val hq: String = "",
    val lobbyCode: String = ""
)

@Serializable
data class CrewCategory(
    val name: String,
    val contacts: List<CrewContact> = emptyList()
)

@Serializable
data class LobbyInfo(
    val code: String,
    val name: String,
    val division: String = "Bilaspur Division - SECR",
    val description: String = "",
    val categories: List<CrewCategory> = emptyList()
)

@Serializable
data class DirectoryData(
    val lobbies: List<LobbyInfo> = emptyList()
)
