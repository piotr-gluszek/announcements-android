package com.piotrgluszek.announcementboard.injection

import com.google.gson.Gson
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.repositories.AnnouncementRepository
import com.piotrgluszek.announcementboard.view.*
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApiModule::class, AppModule::class])
@Singleton
interface ApiComponent {
    fun inject(login: Login)
    fun inject(registration: Registration)
    fun inject(singleAnnouncement: SingleAnnouncement)
    fun inject(board: Board)
    fun inject(editAnnouncement: EditAnnouncementActivity)
    fun gson(): Gson
    fun api(): AnnouncementsApi
    fun repository(): AnnouncementRepository

}