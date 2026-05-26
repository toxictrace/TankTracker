package com.toxictrace.tanktracker.auth

import android.content.Context
import android.content.SharedPreferences

object AuthManager {

    private const val PREFS_NAME = "tanktracker_auth"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_ACCOUNT_ID   = "account_id"
    private const val KEY_NICKNAME     = "nickname"
    private const val KEY_EXPIRES_AT   = "expires_at"

    const val APP_ID = "f21d8f6446559bf7c2bb4f61003e9d28"
    const val REDIRECT_URI = "https://tanktracker.wgauth.app/"
    const val AUTH_URL = "https://api.worldoftanks.eu/wot/auth/login/" +
            "?application_id=$APP_ID" +
            "&redirect_uri=$REDIRECT_URI" +
            "&display=page"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveAuth(context: Context, accessToken: String, accountId: Long, nickname: String, expiresAt: Long) {
        prefs(context).edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putLong(KEY_ACCOUNT_ID, accountId)
            .putString(KEY_NICKNAME, nickname)
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    fun getAccessToken(context: Context): String? =
        prefs(context).getString(KEY_ACCESS_TOKEN, null)

    fun getAccountId(context: Context): Long =
        prefs(context).getLong(KEY_ACCOUNT_ID, -1L)

    fun getNickname(context: Context): String? =
        prefs(context).getString(KEY_NICKNAME, null)

    fun getExpiresAt(context: Context): Long =
        prefs(context).getLong(KEY_EXPIRES_AT, 0L)

    fun isLoggedIn(context: Context): Boolean {
        val token = getAccessToken(context) ?: return false
        if (token.isEmpty()) return false
        val expiresAt = getExpiresAt(context)
        if (expiresAt > 0 && System.currentTimeMillis() / 1000 > expiresAt) return false
        return getAccountId(context) != -1L
    }

    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
