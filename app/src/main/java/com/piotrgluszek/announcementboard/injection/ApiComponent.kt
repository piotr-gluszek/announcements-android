package com.piotrgluszek.announcementboard.injection

import android.app.Application
import com.piotrgluszek.announcementboard.view.Login
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApiModule::class])
@Singleton
interface ApiComponent {
    fun inject(login: Login)
}