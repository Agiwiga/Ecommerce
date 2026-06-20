package com.example.ecommerce.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    fun saveLogin(userId: Int, userName: String) {
        preferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Int {
        return preferences.getInt(KEY_USER_ID, 0)
    }

    fun getUserName(): String {
        return preferences.getString(KEY_USER_NAME, "") ?: ""
    }

    fun logout() {
        preferences.edit()
            .clear()
            .apply()
    }

    companion object {
        private const val PREF_NAME = "ecommerce_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
    }
}
