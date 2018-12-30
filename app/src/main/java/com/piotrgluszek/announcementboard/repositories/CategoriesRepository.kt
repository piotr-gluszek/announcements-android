package com.piotrgluszek.announcementboard.repositories

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.dto.Category
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.interfaces.CanReact
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CategoriesRepository {
    companion object {
        const val LOG_TAG = "CategoriesRepository"
    }

    @Inject
    lateinit var api: AnnouncementsApi
    val categories: MutableLiveData<List<Category>> = MutableLiveData()

    init {
        App.component.inject(this)
    }

    fun getAll(callback: CanReact? = null): MutableLiveData<List<Category>> {
        callApiForCategories(callback)
        return categories
    }

    private fun callApiForCategories(callback: CanReact? = null) {
        api.getAllCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: retrofit2.Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    categories.value = response.body()
                    callback?.onSuccess(Action.GET_MULTIPLE, categories.value)
                } else {
                    Log.d(LOG_TAG, "Failed to fetch categories list")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Category>>, t: Throwable) {
                Log.e(LOG_TAG, t.message)
            }
        })
    }
}
