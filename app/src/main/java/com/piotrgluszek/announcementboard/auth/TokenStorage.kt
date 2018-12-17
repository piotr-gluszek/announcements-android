package com.piotrgluszek.announcementboard.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.piotrgluszek.announcementboard.R

class TokenStorage(var token: String?, var context: Context, val sharedPreferences: SharedPreferences) {
    companion object {
        val LOG_TAG = "TOKEN_STORAGE"
        val TOKEN_STORED_TEXT = "Token stored [%s]"
    }

    fun store(token: String?) {
        sharedPreferences.edit().putString(context.resources.getString(R.string.jwt), token).apply()
        Log.d(LOG_TAG, String.format(TOKEN_STORED_TEXT, token))
    }
    fun remove(){
        sharedPreferences.edit().remove(context.resources.getString(R.string.jwt)).apply()
    }
}