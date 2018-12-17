package com.piotrgluszek.announcementboard.dto

class Page<T>(val content: ArrayList<T>,
              val totalPages: Long,
              val totalElements: Long,
              val number: Long,
              val first: Boolean,
              val last: Boolean )