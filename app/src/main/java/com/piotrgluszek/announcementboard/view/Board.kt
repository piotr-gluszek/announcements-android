package com.piotrgluszek.announcementboard.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.Gson
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.adapters.AnnouncementListAdapter
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.dto.ApiMessage
import com.piotrgluszek.announcementboard.extenstions.toast
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.viewmodel.AnnouncementsViewModel
import kotlinx.android.synthetic.main.activity_board.*
import okhttp3.ResponseBody
import retrofit2.Converter
import javax.inject.Inject

class Board : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "BOARD_ACTIVITY"
        const val REQ_FAIL = "Failed to send request: %s"
        const val JSON_ANNOUNCEMENT = "jsonSerializedAnnouncement"
        const val UPDATE_ANNOUNCEMENT = 1
        const val ID = "id"
        const val ACTION = "action"
        const val ACTION_UPDATE = "update"
        const val ACTION_CREATE = "create"
    }

    @Inject
    lateinit var converter: Converter<ResponseBody, ApiMessage>
    @Inject
    lateinit var gson: Gson

    private lateinit var announcementsViewModel: AnnouncementsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        App.component.inject(this)

        val adapter = AnnouncementListAdapter(
            this,
            R.layout.list_view_announcements
        )
        announcements_list.adapter = adapter

        announcementsViewModel = ViewModelProviders.of(this).get(AnnouncementsViewModel::class.java)
        announcementsViewModel.announcements.observe(
            this, Observer<ArrayList<Announcement>> {
                toast("There was a change in announcement list")
                it?.let {
                    adapter.setAnnouncements(it)
                }
                Log.v(LOG_TAG, "Observer notified")
            }
        )
        Log.v(LOG_TAG, "ON CREATED EXECUTED")
    }

    fun onListItemEdit(id: Long) {
        val intent = Intent(this, EditAnnouncementActivity::class.java)
        intent.putExtra(ID, id)
        intent.putExtra(ACTION, ACTION_UPDATE)
        startActivity(intent)
    }

    fun onListItemRemove() {

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == UPDATE_ANNOUNCEMENT && resultCode == RESULT_OK) {
//            val updatedAnnouncement = gson.fromJson(data?.getStringExtra(JSON_ANNOUNCEMENT), Announcement::class.java)
//
//            for (announcement in announcementsViewModel.announcements.value as ArrayList<Announcement>) {
//                if (announcement.id == updatedAnnouncement.id)
//                    announcementsViewModel.announcements.value
//
//            }
//            val iterator = (announcementsViewModel.announcements.value as ArrayList<Announcement>).listIterator()
//            while (iterator.hasNext()) {
//                val next = iterator.next()
//                if (next.id == updatedAnnouncement.id) {
//                    iterator.set(updatedAnnouncement)
//                }
//            }
//
//        }
//    }
}
