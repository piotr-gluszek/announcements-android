package com.piotrgluszek.announcementboard.dto

import java.sql.Timestamp

class Announcement(val id : Long,
                   val title : String,
                   val description : String,
                   val announcer : String,
                   val categories : List<Category>,
                   val views : Long,
                   val date : Timestamp,
                   val photo : String)