package com.piotrgluszek.announcementboard.dto

import java.io.Serializable

class User(val id : Long,
           val name : String?,
           val photo : String?,
           val mail : String?,
           val phone : String?): Serializable