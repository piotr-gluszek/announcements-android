package com.piotrgluszek.announcementboard.repositories

import android.util.Log
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.dto.ApiMessage
import com.piotrgluszek.announcementboard.dto.RegistrationData
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.exceptions.user.UsernameAlreadyExists
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.interfaces.CanReact
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import javax.inject.Inject

class UserRepository {
    companion object {
        const val LOG_TAG = "UserRepository"
    }

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


}