package com.piotrgluszek.announcementboard.dto

class Announcement(
    var id: Long? = null,
    var title: String? = null,
    var description: String? = null,
    var announcer: User? = null,
    var categories: List<Category>? = null,
    var views: Long? = null,
    var date: Long? = null,
    var photo: String? = null
)