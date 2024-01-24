package com.example.safediary

import android.content.Context

class SharedPreferencesHelper(context: Context) {

    companion object {
        private const val PREFS_FILENAME = "prefs"
        private const val TOKEN = "token"
        private const val APP_ID = "appId"
    }

    private val prefs = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var token: String?
        get() = getString(TOKEN, null)
        set(value) {
            saveString(TOKEN, value)
        }

    var appId: String?
        get() = getString(APP_ID, null)
        set(value) {
            saveString(APP_ID, value)
        }


    private fun saveString(key: String, value: String?) {
        prefs.edit().putString(key, value).apply()
    }
    private fun getString(key: String, defaultValue: String?): String? = prefs.getString(key, defaultValue)
}