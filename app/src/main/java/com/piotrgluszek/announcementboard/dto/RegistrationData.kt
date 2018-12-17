package com.piotrgluszek.announcementboard.dto

class RegistrationData(val username: String,
                       val password: String,
                       val photo: String? = null,
                       val name: String? = null,
                       val mail: String? = null ,
                       val phone: String? = null)