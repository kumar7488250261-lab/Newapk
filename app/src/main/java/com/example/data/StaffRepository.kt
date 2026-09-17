package com.example.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class StaffRepository(private val context: Context) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun getLobbies(): List<Lobby> {
        return try {
            val json = context.assets.open("kharsia_directory.json").bufferedReader().use { it.readText() }
            val adapter = moshi.adapter(DirectoryResponse::class.java)
            val response = adapter.fromJson(json)
            response?.lobbies ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
