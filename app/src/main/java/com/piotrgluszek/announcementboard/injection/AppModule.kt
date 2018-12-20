package com.piotrgluszek.announcementboard.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private var context: Context) {
    @Provides
    @Singleton
    fun context(): Context {
        return context
    }
}