package com.piotrgluszek.announcementboard.communication

import retrofit2.Call
import retrofit2.http.GET

interface AnnouncementsApi {
    @GET("/announcements")
    fun getAllAnnouncements(): Call<Any>
}