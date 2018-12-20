package com.piotrgluszek.announcementboard.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.google.gson.Gson
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.adapters.AnnouncementListAdapter
import com.piotrgluszek.announcementboard.auth.TokenStorage
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.dto.ApiMessage
import com.piotrgluszek.announcementboard.dto.Page
import com.piotrgluszek.announcementboard.injection.ApiComponent
import com.piotrgluszek.announcementboard.injection.ApiModule
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.injection.DaggerApiComponent
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import javax.inject.Inject

class Board : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "BOARD_ACTIVITY"
        const val REQ_FAIL = "Failed to send request: %s"
    }

    @Inject
    lateinit var announcementApi: AnnouncementsApi
    @Inject
    lateinit var tokenStorage: TokenStorage
    @Inject
    lateinit var converter: Converter<ResponseBody, ApiMessage>
    @Inject
    lateinit var gson: Gson

    private lateinit var listView: ListView
    var announcements: ArrayList<Announcement>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        getApiComponent()?.inject(this)
        listView = findViewById(R.id.announcements_list)
        announcementApi.getAllAnnouncements().enqueue(object : Callback<Page<Announcement>> {
            override fun onFailure(call: Call<Page<Announcement>>, t: Throwable) {
                Log.e(LOG_TAG, String.format(REQ_FAIL, t.message), t)
                Toast.makeText(this@Board, "Connection problem occurred", Toast.LENGTH_SHORT)
            }

            override fun onResponse(call: Call<Page<Announcement>>, response: Response<Page<Announcement>>) {
                when (response.code()) {
                    200 -> {
                        announcements = response.body()?.content
                        val adapter =
                            AnnouncementListAdapter(this@Board, R.layout.list_view_announcements, announcements!!)
                        listView.adapter = adapter
                    }
                    403 -> {
                        Toast.makeText(this@Board, converter.convert(response.errorBody())?.message, Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@Board, Login::class.java)
                        startActivity(intent)
                    }
                }
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this@Board, SingleAnnouncement::class.java)
            val serializedAnnouncement = gson.toJson(announcements?.get(position))
            intent.putExtra("selectedAnnouncement", serializedAnnouncement)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        announcementApi.getAllAnnouncements().enqueue(object : Callback<Page<Announcement>> {
            override fun onFailure(call: Call<Page<Announcement>>, t: Throwable) {
                Log.e(LOG_TAG, String.format(REQ_FAIL, t.message), t)
                Toast.makeText(this@Board, "Connection problem occurred", Toast.LENGTH_SHORT)
            }

            override fun onResponse(call: Call<Page<Announcement>>, response: Response<Page<Announcement>>) {
                when (response.code()) {
                    200 -> {
                        announcements = response.body()?.content
                        val adapter =
                            AnnouncementListAdapter(this@Board, R.layout.list_view_announcements, announcements!!)
                        listView.adapter = adapter
                    }
                    403 -> {
                        Toast.makeText(this@Board, converter.convert(response.errorBody())?.message, Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@Board, Login::class.java)
                        startActivity(intent)
                    }
                }
            }
        })

    }

    private fun getApiComponent(): ApiComponent? {
        return App.component

    }
}
