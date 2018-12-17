package com.piotrgluszek.announcementboard.injection

import com.piotrgluszek.announcementboard.view.Board
import com.piotrgluszek.announcementboard.view.Login
import com.piotrgluszek.announcementboard.view.Registration
import com.piotrgluszek.announcementboard.view.SingleAnnouncement
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApiModule::class])
@Singleton
interface ApiComponent {
    fun inject(login: Login)
    fun inject(registration: Registration)
    fun inject(singleAnnouncement: SingleAnnouncement)
    fun inject(board: Board)

}