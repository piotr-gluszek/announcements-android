package com.piotrgluszek.announcementboard.extenstions

import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

fun String?.replaceIfEmpty(replacement: String?): String? {
    if (this!!.isEmpty())
        return replacement
    return this
}

fun Timestamp.formattedDateString(format: String): String {
    val sdf = SimpleDateFormat(format)
    val date = Date(this.nanos * 1000L)
    return sdf.format(date)
}