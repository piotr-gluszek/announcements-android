package com.piotrgluszek.announcementboard.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.dto.Page
import com.piotrgluszek.announcementboard.extenstions.notifyObservers
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.view.Login
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnnouncementRepository {

    companion object {
        const val REQ_FAIL = "Request failed: %s"
        const val LOG_TAG = "AnnouncementRepository"
    }

    private val api = App.component.api()
    private val announcements: MutableLiveData<ArrayList<Announcement>> = MutableLiveData<ArrayList<Announcement>>()

    fun getAll(): LiveData<ArrayList<Announcement>> {
        announcements.value?.let {
            if(it.isNotEmpty())
                return announcements
        }
        api.getAllAnnouncements().enqueue(object : Callback<Page<Announcement>> {
            override fun onFailure(call: Call<Page<Announcement>>, t: Throwable) {
                Log.e(LOG_TAG, String.format(REQ_FAIL, t.message), t)
            }

            override fun onResponse(call: Call<Page<Announcement>>, response: Response<Page<Announcement>>) {
                when (response.code()) {
                    200 -> {
                        announcements.setValue(response.body()?.content)
                    }
                }
            }
        })
        return announcements
    }

    fun update(id: Long, updatedAnnouncement: Announcement) {

        api.updateAnnoncement(id, updatedAnnouncement).enqueue(object : Callback<Announcement> {
            override fun onFailure(call: Call<Announcement>, t: Throwable) {
                Log.e(Login.LOG_TAG, String.format(Login.REQ_FAIL, t.message), t)
            }

            override fun onResponse(call: Call<Announcement>, response: Response<Announcement>) {
                when (response.code()) {
                    200 -> {
                        Log.v(LOG_TAG, "Update successful")
                    }
                    403 -> {
                        Log.e(LOG_TAG, "Update failed. User not authorized")
                    }
                }
            }
        })
        announcements.value?.let {
            it.filter { a -> a.id == id }.first().apply {
                title = updatedAnnouncement.title
                description = updatedAnnouncement.description
                updatedAnnouncement.photo.let {
                    photo = updatedAnnouncement.photo
                }
            }
        }
        announcements.notifyObservers()
    }
}