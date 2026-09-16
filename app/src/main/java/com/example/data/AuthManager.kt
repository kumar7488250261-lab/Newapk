package com.example.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user login session for Kharsia Lobby app.
 */
class AuthManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("kharsia_lobby_auth", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userId: String
        get() = prefs.getString(KEY_USER_ID, "KHS1234") ?: "KHS1234"
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    fun login(id: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_ID, id)
            .apply()
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_ID)
            .apply()
    }
}
