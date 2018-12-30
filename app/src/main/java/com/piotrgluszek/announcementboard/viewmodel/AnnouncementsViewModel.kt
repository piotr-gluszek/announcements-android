package com.piotrgluszek.announcementboard.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.injection.App

class AnnouncementsViewModel : ViewModel() {
    private val announcementRepository = App.component.announcementRepository()
    val announcements: LiveData<ArrayList<Announcement>> by lazy { announcementRepository.getAll() }
    fun update(id: Long, announcement: Announcement) {
        announcementRepository.update(id, announcement)
    }
    fun create(announcement: Announcement){
        announcementRepository.create(announcement)
    }
    fun delete(id: Long){
        announcementRepository.delete(id)
    }
}