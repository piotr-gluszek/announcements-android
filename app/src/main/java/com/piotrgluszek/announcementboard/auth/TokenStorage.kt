package com.piotrgluszek.announcementboard.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.piotrgluszek.announcementboard.R
import io.jsonwebtoken.Jwts


class TokenStorage(var token: String?, var context: Context, val sharedPreferences: SharedPreferences) {
    companion object {
        const val LOG_TAG = "TOKEN_STORAGE"
        const val TOKEN_STORED_TEXT = "Token stored [%s]"
    }

    fun store(token: String?) {
        this.token = token
        sharedPreferences.edit().putString(context.resources.getString(R.string.jwt), token).apply()
        Log.d(LOG_TAG, String.format(TOKEN_STORED_TEXT, this.token))
    }

    fun remove() {
        sharedPreferences.edit().remove(context.resources.getString(R.string.jwt)).apply()
        this.token = null
    }

    fun readUserId(token: String): Long {
        return Jwts.parser()
            .setSigningKey(context.getString(R.string.jwt_secret))
            .parseClaimsJws(token)
            .body
            .subject
            .toLong()
    }
}