package com.piotrgluszek.announcementboard.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import br.com.ilhasoft.support.validation.Validator
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
    private val announcements: MutableLiveData<ArrayList<Announcement>> = MutableLiveData()

    fun getAll(): LiveData<ArrayList<Announcement>> {
//        announcements.value?.let {
//            if(it.isNotEmpty())
//                return announcements
//        }
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
                Log.e(LOG_TAG, String.format(REQ_FAIL, t.message), t)
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

    fun create(announcement: Announcement){
        api.createAnnouncement(announcement).enqueue(object: Callback<Announcement> {
            override fun onFailure(call: Call<Announcement>, t: Throwable) {
                Log.e(LOG_TAG, String.format(REQ_FAIL, t.message), t)
            }

            override fun onResponse(call: Call<Announcement>, response: Response<Announcement>) {
                when (response.code()) {
                    201 -> {
                        this@AnnouncementRepository.getAll()
                        Log.v(LOG_TAG, "Creation successful")
                    }
                    403 -> {
                        Log.e(LOG_TAG, "Creation failed. User not authorized")
                    }
                }
            }
        })
    }

    fun delete(id: Long){
        api.deleteAnnouncement(id).enqueue(object: Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(LOG_TAG, String.format(REQ_FAIL, t.message), t)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> {
                        announcements.value?.let{
                            announcements.value = (announcements.value as ArrayList<Announcement>).filter { a -> a.id != id }.toCollection(ArrayList())
                            announcements.notifyObservers()
                            Log.v(LOG_TAG, "Deletion successful")
                        }

                    }
                    403 -> {
                        Log.e(LOG_TAG, "Deletion failed. User not authorized")
                    }
                }
            }
        })
    }
}