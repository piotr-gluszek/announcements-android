package com.piotrgluszek.announcementboard.extenstions

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