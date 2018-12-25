package com.piotrgluszek.announcementboard.interfaces

import com.piotrgluszek.announcementboard.enums.Action

/**
 * Handling async requests.
 */
interface CanReact {
    fun onSuccess(action: Action, data: Any? = null)
    fun onFail(action: Action, t: Throwable)
}