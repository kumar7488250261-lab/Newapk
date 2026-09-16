package com.example.ui.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ContactActions {

    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        val cleanNumber = phoneNumber.replace("[^0-9+]".toRegex(), "")
        if (cleanNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
            }
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Contact Number") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
    }

    fun openWhatsApp(context: Context, phoneNumber: String) {
        val cleanNumber = phoneNumber.replace("[^0-9]".toRegex(), "")
        val formattedNumber = if (cleanNumber.length == 10) "91$cleanNumber" else cleanNumber
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }
}
