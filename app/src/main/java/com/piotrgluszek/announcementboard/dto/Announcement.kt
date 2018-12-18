package com.piotrgluszek.announcementboard.dto

import java.sql.Timestamp

class Announcement(val id : Long,
                   val title : String,
                   val description : String,
                   val announcer : User,
                   val categories : List<Category>,
                   val views : Long,
                   val date : Long,
                   val photo : String)