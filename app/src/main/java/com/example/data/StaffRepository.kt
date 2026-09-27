package com.example.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class StaffRepository(private val context: Context) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val cachedLobbies: List<Lobby> by lazy {
        loadLobbies()
    }

    private val cachedStations: List<StationContact> by lazy {
        loadStations()
    }

    private val phoneMap: Map<String, String> by lazy {
        val map = mutableMapOf<String, String>()
        for (lobby in cachedLobbies) {
            for (category in lobby.categories) {
                for (contact in category.contacts) {
                    val phone = if (contact.mobile.isNotBlank()) contact.mobile else contact.cug
                    if (contact.name.isNotBlank() && phone.isNotBlank()) {
                        map[contact.name.trim().uppercase()] = phone
                        val simplified = contact.name.replace(Regex("""\b(G-?12|COLP|\d+)\b""", RegexOption.IGNORE_CASE), "").trim().uppercase()
                        if (simplified.isNotEmpty()) {
                            map[simplified] = phone
                        }
                    }
                }
            }
        }
        map
    }

    private val cachedCrewMembers: List<CrewMember> by lazy {
        loadCrewMaster()
    }

    private fun loadLobbies(): List<Lobby> {
        return try {
            val json = context.assets.open("kharsia_directory.json").bufferedReader().use {
                it.readText()
            }
            val adapter = moshi.adapter(DirectoryResponse::class.java)
            adapter.fromJson(json)?.lobbies ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun loadStations(): List<StationContact> {
        return try {
            val json = context.assets.open("secr_stations.json").bufferedReader().use {
                it.readText()
            }
            val adapter = moshi.adapter(StationResponse::class.java)
            adapter.fromJson(json)?.stations ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun loadCrewMaster(): List<CrewMember> {
        return try {
            val json = context.assets.open("kharsia_crew_master.json").bufferedReader().use {
                it.readText()
            }
            val listType = Types.newParameterizedType(List::class.java, CrewMember::class.java)
            val adapter = moshi.adapter<List<CrewMember>>(listType)
            val rawList = adapter.fromJson(json) ?: emptyList()
            rawList.map { crew ->
                if (crew.mobile.isNotBlank()) {
                    crew
                } else {
                    val phone = phoneMap[crew.name.trim().uppercase()]
                        ?: phoneMap[crew.name.replace(Regex("""\b(G-?12|COLP|\d+)\b""", RegexOption.IGNORE_CASE), "").trim().uppercase()]
                        ?: ""
                    crew.copy(mobile = phone)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getLobbies(): List<Lobby> = cachedLobbies

    fun getStations(): List<StationContact> = cachedStations

    fun getControlCenterLobby(): Lobby? {
        return cachedLobbies.find { it.code.equals("CTRL", ignoreCase = true) }
    }

    fun getKharsiaLobby(): Lobby? {
        return cachedLobbies.find { it.code.equals("KHS", ignoreCase = true) }
    }

    fun getOtherLobbies(): List<Lobby> {
        return cachedLobbies.filter { !it.code.equals("CTRL", ignoreCase = true) && !it.code.equals("KHS", ignoreCase = true) }
    }

    fun getEmergencyQuickContacts(): List<StaffContact> {
        return listOf(
            StaffContact("TLC Bilaspur", "Traction Loco Controller (24x7)", "9752442111", "9752442111"),
            StaffContact("TPC Bilaspur", "Traction Power Controller", "9752442115", "9752442115"),
            StaffContact("Chief Controller / CCC", "Divisional Control Office", "9752442110", "9752442110"),
            StaffContact("Kharsia Lobby Desk", "Lobby Master Landline", "07766276100", "07766276100"),
            StaffContact("Test Room / Telecom BSP", "Railway Telecom / Fault Repair", "9752442131", "9752442131"),
            StaffContact("Safety Control / DRM", "Emergency & Safety Helpline", "9752442120", "9752442120")
        )
    }

    fun getCrewMaster(): List<CrewMember> = cachedCrewMembers

    fun findCrewById(crewId: String): CrewMember? {
        val trimmed = crewId.trim().uppercase()
        if (trimmed.isEmpty()) return null

        cachedCrewMembers.find { it.crewId.equals(trimmed, ignoreCase = true) }?.let { return it }

        if (trimmed.all { it.isDigit() }) {
            val withPrefix = "KHS$trimmed"
            cachedCrewMembers.find { it.crewId.equals(withPrefix, ignoreCase = true) }?.let { return it }
        }

        cachedCrewMembers.find { it.crewId.endsWith(trimmed, ignoreCase = true) }?.let { return it }

        return null
    }

    fun findCrewByName(name: String): CrewMember? {
        val trimmed = name.trim()
        if (trimmed.length < 3) return null
        cachedCrewMembers.find { it.name.equals(trimmed, ignoreCase = true) }?.let { return it }
        val matches = cachedCrewMembers.filter { it.name.contains(trimmed, ignoreCase = true) }
        if (matches.size == 1) return matches.first()
        return null
    }

    fun findCrew(query: String): CrewMember? {
        return findCrewById(query) ?: findCrewByName(query)
    }

    fun searchCrew(query: String, limit: Int = 30): List<CrewMember> {
        val q = query.trim().uppercase()
        if (q.isEmpty()) return cachedCrewMembers.take(limit)

        return cachedCrewMembers.filter { crew ->
            crew.crewId.uppercase().contains(q) ||
            crew.name.uppercase().contains(q) ||
            crew.designation.uppercase().contains(q) ||
            crew.category.uppercase().contains(q)
        }.sortedWith(
            compareBy<CrewMember> {
                when {
                    it.crewId.uppercase() == q -> 0
                    it.crewId.uppercase().startsWith(q) -> 1
                    it.name.uppercase().startsWith(q) -> 2
                    else -> 3
                }
            }
        ).take(limit)
    }
}
