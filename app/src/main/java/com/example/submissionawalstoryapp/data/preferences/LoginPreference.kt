package com.example.submissionawalstoryapp.data.preferences

import android.content.Context
import com.example.submissionawalstoryapp.data.response.Login
import com.example.submissionawalstoryapp.data.response.LoginResult


internal class LoginPreference(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setLogin(value: LoginResult) {
        preferences.edit().apply {
            putString(NAME, value.name)
            putString(USER_ID, value.userId)
            putString(TOKEN, value.token)
            apply()
        }
    }

    fun getUser(): LoginResult {
        return LoginResult(
            preferences.getString(USER_ID, null),
            preferences.getString(NAME, null),
            preferences.getString(TOKEN, null)
        )
    }

    fun removeUser() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "login_pref"
        private const val NAME = "name"
        private const val USER_ID = "userId"
        private const val TOKEN = "token"
    }
}
