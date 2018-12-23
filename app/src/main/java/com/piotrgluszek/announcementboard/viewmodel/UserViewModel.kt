package com.piotrgluszek.announcementboard.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.piotrgluszek.announcementboard.dto.RegistrationData
import com.piotrgluszek.announcementboard.dto.User
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.interfaces.CanReact

class UserViewModel : ViewModel() {
    private var user = MutableLiveData<User>()
    private val userRepository = App.component.userRepository()
    fun create(registrationData: RegistrationData, callback: CanReact) {
        userRepository.create(registrationData, callback, Action.CREATE)
    }
}