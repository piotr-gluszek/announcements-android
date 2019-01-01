package com.piotrgluszek.announcementboard.view

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.extenstions.formattedDateString
import com.piotrgluszek.announcementboard.image.ImageConverter
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.viewmodel.AnnouncementsViewModel
import kotlinx.android.synthetic.main.activity_single_announcement.*

class SingleAnnouncement : AppCompatActivity() {


    private lateinit var announcementViewModel: AnnouncementsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_announcement)
        App.component.inject(this)
        announcementViewModel = ViewModelProviders.of(this).get(AnnouncementsViewModel::class.java)
        val position = intent.extras.getInt("id")
        val announcement = announcementViewModel.announcements.value?.get(position)
        announcement?.let{
            setView(it)
            announcementViewModel.incrementViews(announcement.id!!)
        }

    }

    private fun setView(announcement: Announcement){
        val title = findViewById<TextView>(R.id.title)
        title.text = announcement.title
        description.text = announcement.description
        name.text = announcement.announcer?.name
        mail.text = announcement.announcer?.mail
        phone.text = announcement.announcer?.phone
        date.text = "Posted: ${announcement.date?.formattedDateString("dd/MM/yyyy")}"
        views.text = "Views: ${announcement.views}"
        var categoriesAsString =""
        for(category in announcement.categories ?: mutableListOf()){
            categoriesAsString += "#${category.name}\n"
        }
        categories.text=categoriesAsString
        if(announcement.photo!=null)
            photo.setImageBitmap(ImageConverter.fromBase64(announcement.photo as String))
    }
}
