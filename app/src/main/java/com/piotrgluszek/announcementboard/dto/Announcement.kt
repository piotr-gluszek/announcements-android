package com.piotrgluszek.announcementboard.dto

data class Announcement(
    var id: Long? = null,
    var title: String? = null,
    var description: String? = null,
    var announcer: User? = null,
    var categories: MutableList<Category>? = null,
    var views: Long? = null,
    var date: Long? = null,
    var photo: String? = null
)