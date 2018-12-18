package com.piotrgluszek.announcementboard.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.injection.ApiComponent
import com.piotrgluszek.announcementboard.injection.ApiModule
import com.piotrgluszek.announcementboard.injection.DaggerApiComponent
import kotlinx.android.synthetic.main.activity_single_announcement.*
import javax.inject.Inject

class SingleAnnouncement : AppCompatActivity() {

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_announcement)
        getApiComponent().inject(this)

        val serializedAnnouncement = intent.getStringExtra("selectedAnnouncement")
        val announcemet = deserializeAnnouncement(serializedAnnouncement)
        setView(announcemet)

    }
    private fun getApiComponent(): ApiComponent {
        return DaggerApiComponent.builder().apiModule(ApiModule(application)).build();
    }

    private fun deserializeAnnouncement(serializedAnnouncement: String): Announcement{
        return gson.fromJson(serializedAnnouncement, Announcement::class.java)
    }
    private fun setView(announcemet: Announcement){
        val title = findViewById<TextView>(R.id.title)
        title.text = announcemet.title
        description.text = announcemet.description
        name.text = announcemet.announcer.name
        mail.text = announcemet.announcer.mail
        phone.text = announcemet.announcer.phone
        TODO("Set photo and announcer avatar")
    }
}
