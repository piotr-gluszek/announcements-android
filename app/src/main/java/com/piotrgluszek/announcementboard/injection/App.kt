package com.piotrgluszek.announcementboard.injection

import android.app.Application

class App : Application() {
    companion object {
        var component: ApiComponent? = null
    }

    override fun onCreate() {
        super.onCreate()
        component = buildComponent()
    }

    private fun buildComponent(): ApiComponent {
        return DaggerApiComponent.builder()
            .appModule(AppModule(this))
            .apiModule(ApiModule(this))
            .build()
    }
}