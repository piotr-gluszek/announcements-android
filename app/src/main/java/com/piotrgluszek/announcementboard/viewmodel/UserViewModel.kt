package com.piotrgluszek.announcementboard.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.piotrgluszek.announcementboard.dto.Credentials
import com.piotrgluszek.announcementboard.dto.RegistrationData
import com.piotrgluszek.announcementboard.dto.User
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.interfaces.CanReact

class UserViewModel : ViewModel() {
    private val userRepository = App.component.userRepository()
    private var user = MutableLiveData<User>()

    init {
        user = userRepository.authenticatedUser
    }

    fun create(registrationData: RegistrationData, callback: CanReact) {
        userRepository.create(registrationData, callback, Action.CREATE)
    }

    fun authenticate(credentials: Credentials, callback: CanReact) {
        userRepository.authenticate(credentials, callback, Action.AUTHENTICATE)
    }

    fun get(id: Long, callback: CanReact) {
        userRepository.get(id, callback, Action.GET_ONE)
    }
}
