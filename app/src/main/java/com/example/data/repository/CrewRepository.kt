package com.example.data.repository

import android.content.Context
import com.example.data.model.CrewContact
import com.example.data.model.DirectoryData
import com.example.data.model.LobbyInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CrewRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var cachedData: DirectoryData? = null

    suspend fun getDirectoryData(): DirectoryData = withContext(Dispatchers.IO) {
        cachedData?.let { return@withContext it }
        val loaded = try {
            val jsonString = context.assets.open("kharsia_directory.json").bufferedReader().use { it.readText() }
            json.decodeFromString<DirectoryData>(jsonString)
        } catch (e: Exception) {
            DirectoryData()
        }
        cachedData = loaded
        loaded
    }

    suspend fun getLobbies(): List<LobbyInfo> {
        return getDirectoryData().lobbies
    }

    suspend fun getLobby(code: String): LobbyInfo? {
        return getDirectoryData().lobbies.find { it.code.equals(code, ignoreCase = true) }
    }

    suspend fun getAllCrew(): List<CrewContact> {
        val lobbies = getDirectoryData().lobbies
        val result = mutableListOf<CrewContact>()
        for (lobby in lobbies) {
            for (category in lobby.categories) {
                for (contact in category.contacts) {
                    result.add(
                        contact.copy(
                            lobbyCode = if (contact.lobbyCode.isNotEmpty()) contact.lobbyCode else lobby.code,
                            category = if (contact.category.isNotEmpty()) contact.category else category.name,
                            hq = if (contact.hq.isNotEmpty()) contact.hq else lobby.code
                        )
                    )
                }
            }
        }
        return result
    }
}
