package com.piotrgluszek.announcementboard.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.dto.ApiMessage
import com.piotrgluszek.announcementboard.dto.Credentials
import com.piotrgluszek.announcementboard.dto.RegistrationData
import com.piotrgluszek.announcementboard.dto.User
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.exceptions.user.InvalidCredentials
import com.piotrgluszek.announcementboard.exceptions.user.UsernameAlreadyExists
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.interfaces.CanReact
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class UserRepository {
    companion object {
        const val LOG_TAG = "UserRepository"
    }

    var authenticatedUser: MutableLiveData<User> = MutableLiveData<User>()
    @Inject
    lateinit var api: AnnouncementsApi
    @Inject
    lateinit var converter: Converter<ResponseBody, ApiMessage>

    init {
        App.component.inject(this)
    }

    fun create(registrationData: RegistrationData, callback: CanReact, action: Action) {
        api.register(registrationData).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) callback.onSuccess(action)
                else {
                    converter.convert(response.errorBody())?.message?.let {
                        when (response.code()) {
                            409 -> callback.onFail(action, UsernameAlreadyExists(it))
                        }
                        Log.d(LOG_TAG, it)
                    }
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                callback.onFail(action, t)
                Log.e(LOG_TAG, t.message, t)
            }
        })
    }

    fun authenticate(credentials: Credentials, callback: CanReact, action: Action) {
        api.login(credentials).enqueue(object : Callback<ApiMessage> {
            override fun onFailure(call: Call<ApiMessage>, t: Throwable) {
                callback.onFail(action, t)
                Log.e(LOG_TAG, t.message, t)
            }

            override fun onResponse(call: Call<ApiMessage>, response: Response<ApiMessage>) {
                when (response.code()) {
                    200 -> {
                        val token = response.body()?.message
                        callback.onSuccess(action, token)
                    }
                    403 -> {
                        converter.convert(response.errorBody())?.message?.let {
                            callback.onFail(action, InvalidCredentials(it))
                            Log.d(LOG_TAG, it)
                        }
                    }
                }
            }
        })
    }

    fun get(id: Long, callback: CanReact, action: Action) {
        api.getUserById(id).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    authenticatedUser.value = response.body()
                    callback.onSuccess(action, response.body())
                } else {
                    when (response.code()) {
                        404 -> {
                            converter.convert(response.errorBody())?.message?.let {
                                callback.onFail(action, NoSuchElementException(it))
                                Log.d(LOG_TAG, it)
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback.onFail(action, t)
                Log.e(LOG_TAG, t.message, t)
            }
        })
    }
}