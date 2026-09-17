package com.example.data.equipment

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

class InChargeAuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("incharge_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val DEFAULT_PIN = "1234"
        private const val TIMEOUT_MS = 15 * 60 * 1000L // 15 minutes session
    }

    private var sessionExpiryTime: Long = 0
    private var authenticatedUser: String? = null

    val isSessionValid: Boolean
        get() = System.currentTimeMillis() < sessionExpiryTime && authenticatedUser != null

    val currentAdminUser: String
        get() = authenticatedUser ?: "Supervisor"

    fun verifyPin(enteredPin: String, adminId: String = "Lobby In-Charge"): Boolean {
        // Accepts configured PIN or default PIN "1234"
        val savedHash = prefs.getString("pin_hash", null)
        val isValid = if (savedHash == null) {
            enteredPin == DEFAULT_PIN
        } else {
            hashPin(enteredPin) == savedHash || enteredPin == DEFAULT_PIN
        }

        if (isValid) {
            sessionExpiryTime = System.currentTimeMillis() + TIMEOUT_MS
            authenticatedUser = adminId
        }
        return isValid
    }

    fun endSession() {
        sessionExpiryTime = 0
        authenticatedUser = null
    }

    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
