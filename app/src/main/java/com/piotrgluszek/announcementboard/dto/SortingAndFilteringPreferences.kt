package com.piotrgluszek.announcementboard.dto

class SortingAndFilteringPreferences(
    var category: Category? = null,
    val sortingOptions: MutableMap<String, String?> = mutableMapOf()
){
    fun getSortingOptionsVarargs(): MutableList<String> {
        val varargs = mutableListOf<String>()
        for (key in sortingOptions.keys) {
            if(sortingOptions[key] != null)
            varargs.add(key + "," + sortingOptions[key])
        }
        return varargs
    }
}