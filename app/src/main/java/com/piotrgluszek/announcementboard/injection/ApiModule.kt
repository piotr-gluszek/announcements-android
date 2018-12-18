package com.piotrgluszek.announcementboard.injection

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.piotrgluszek.announcementboard.auth.AuthInterceptor
import com.piotrgluszek.announcementboard.auth.TokenStorage
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.dto.ApiMessage
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule(val application: Application) {

    @Provides
    @Singleton
    fun providesSharedPreferences():
            SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(): Cache {
        val cacheSize = 10 * 1024 * 1024L // 10 MiB
        return Cache(application.cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache, tokenStorage: TokenStorage): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.cache(cache)
        client.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        client.addInterceptor(AuthInterceptor(tokenStorage))
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("http://192.168.1.246:8181")
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): AnnouncementsApi {
        return retrofit.create(AnnouncementsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenStorage(sharedPreferences: SharedPreferences): TokenStorage {
        return TokenStorage(null, application.applicationContext, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideApiMessageConverter(retrofit: Retrofit): Converter<ResponseBody, ApiMessage> {
        return retrofit.responseBodyConverter(ApiMessage::class.java, emptyArray());
    }
}