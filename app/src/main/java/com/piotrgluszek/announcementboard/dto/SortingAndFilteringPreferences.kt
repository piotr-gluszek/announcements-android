package com.piotrgluszek.announcementboard.dto

import android.arch.lifecycle.MutableLiveData

class SortingAndFilteringPreferences(
    var category: Category? = null,
    val sortingOptions: MutableMap<String, String?> = mutableMapOf(),
    var number: Long = 0,
    var size: Int = 5,
    var isLast: MutableLiveData<Boolean> = MutableLiveData(),
    var isFirst: MutableLiveData<Boolean> = MutableLiveData()
) {

    fun getSortingOptionsVarargs(): MutableList<String> {
        val varargs = mutableListOf<String>()
        for (key in sortingOptions.keys) {
            if (sortingOptions[key] != null)
                varargs.add(key + "," + sortingOptions[key])
        }
        return varargs
    }
}