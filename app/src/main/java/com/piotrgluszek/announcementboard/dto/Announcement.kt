package com.piotrgluszek.announcementboard.dto

import java.sql.Timestamp

class Announcement(val id : Long? = null,
                   val title : String? = null,
                   val description : String? = null,
                   val announcer : User? = null,
                   val categories : List<Category>? = null,
                   val views : Long? = null,
                   val date : Long? = null ,
                   val photo : String? = null)