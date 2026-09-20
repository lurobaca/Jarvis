package com.lurobaca.jarvis.data

import android.content.Context

class PicovoiceAccessKeyStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun get(): String = preferences.getString(KEY_ACCESS_KEY, "").orEmpty()

    fun save(value: String) {
        preferences.edit().putString(KEY_ACCESS_KEY, value.trim()).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "jarvis_private_settings"
        const val KEY_ACCESS_KEY = "picovoice_access_key"
    }
}
