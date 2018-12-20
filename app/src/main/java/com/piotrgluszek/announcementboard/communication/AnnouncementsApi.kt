package com.piotrgluszek.announcementboard.communication


import com.piotrgluszek.announcementboard.dto.*
import retrofit2.Call
import retrofit2.http.*

interface AnnouncementsApi {
    @POST("/login")
    fun login(@Body credentials: Credentials): Call<ApiMessage>

    @GET("/announcements")
    fun getAllAnnouncements(): Call<Page<Announcement>>

    @POST("/register")
    fun register(@Body registrationData: RegistrationData): Call<Any>

    @PUT("/announcements/{id}")
    fun updateAnnoncement(@Path("id") id: Long, @Body updatedAnnouncement: Announcement): Call<Announcement>


}