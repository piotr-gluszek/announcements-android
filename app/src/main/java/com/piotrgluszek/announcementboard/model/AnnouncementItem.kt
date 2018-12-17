package com.piotrgluszek.announcementboard.model

import com.piotrgluszek.announcementboard.dto.Category
import java.sql.Timestamp

class AnnouncementItem(
    val id: Long,
    var title: String,
    var description: String,
    val announcer: String,
    var categories: List<Category>,
    val views: Long,
    val date: Timestamp,
    var photo: String
)