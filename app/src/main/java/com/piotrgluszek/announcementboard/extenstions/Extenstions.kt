package com.piotrgluszek.announcementboard.extenstions

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.widget.Toast
import java.sql.Date
import java.text.SimpleDateFormat

fun String?.replaceIfEmpty(replacement: String?): String? {
    if (this!!.isEmpty())
        return replacement
    return this
}

fun Long.formattedDateString(format: String): String {
    val sdf = SimpleDateFormat(format)
    val date = Date(this)
    return sdf.format(date)
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun <T> MutableLiveData<T>.notifyObservers() {
    this.value = this.value
}