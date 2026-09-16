package com.example.data

import android.content.Context
import org.json.JSONObject
import java.io.InputStreamReader

class StaffRepository(private val context: Context) {

    private var cachedLobbies: List<Lobby>? = null

    fun getLobbies(): List<Lobby> {
        cachedLobbies?.let { return it }

        val lobbies = mutableListOf<Lobby>()
        try {
            val inputStream = context.assets.open("staff_directory.json")
            val jsonString = InputStreamReader(inputStream, Charsets.UTF_8).use { it.readText() }
            val root = JSONObject(jsonString)
            val lobbiesArray = root.getJSONArray("lobbies")

            for (i in 0 until lobbiesArray.length()) {
                val lobbyObj = lobbiesArray.getJSONObject(i)
                val id = lobbyObj.optString("id", "")
                val name = lobbyObj.optString("name", "")
                val code = lobbyObj.optString("code", "")
                val catArray = lobbyObj.getJSONArray("categories")

                val categories = mutableListOf<LobbyCategory>()
                var lobbyTotal = 0

                for (j in 0 until catArray.length()) {
                    val catObj = catArray.getJSONObject(j)
                    val catName = catObj.optString("name", "")
                    val catFullName = catObj.optString("fullName", catName)
                    val contactsArray = catObj.getJSONArray("contacts")

                    val contacts = mutableListOf<StaffContact>()
                    for (k in 0 until contactsArray.length()) {
                        val contactObj = contactsArray.getJSONObject(k)
                        val staffName = contactObj.optString("name", "")
                        val mobile = contactObj.optString("mobile", "")
                        contacts.add(
                            StaffContact(
                                name = staffName,
                                mobile = mobile,
                                category = catName,
                                lobbyCode = code,
                                lobbyName = name
                            )
                        )
                    }
                    lobbyTotal += contacts.size
                    categories.add(
                        LobbyCategory(
                            name = catName,
                            fullName = catFullName,
                            contacts = contacts
                        )
                    )
                }

                lobbies.add(
                    Lobby(
                        id = id,
                        name = name,
                        code = code,
                        categories = categories,
                        totalContacts = lobbyTotal
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cachedLobbies = lobbies
        return lobbies
    }

    fun getLobbyById(id: String): Lobby? {
        return getLobbies().find { it.id.equals(id, ignoreCase = true) || it.code.equals(id, ignoreCase = true) }
    }

    fun searchAllStaff(query: String): List<StaffContact> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        val results = mutableListOf<StaffContact>()
        for (lobby in getLobbies()) {
            for (category in lobby.categories) {
                for (contact in category.contacts) {
                    if (contact.name.lowercase().contains(q) ||
                        contact.mobile.contains(q) ||
                        contact.category.lowercase().contains(q) ||
                        contact.lobbyName.lowercase().contains(q) ||
                        contact.lobbyCode.lowercase().contains(q)
                    ) {
                        results.add(contact)
                    }
                }
            }
        }
        return results
    }
}
