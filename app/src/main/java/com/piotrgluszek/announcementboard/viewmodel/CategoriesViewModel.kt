package com.piotrgluszek.announcementboard.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.piotrgluszek.announcementboard.dto.Category
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.interfaces.CanReact
import com.piotrgluszek.announcementboard.repositories.CategoriesRepository

class CategoriesViewModel : ViewModel(){
    companion object {
        const val LOG_TAG = "CategoriesViewModel"
    }

    private val categoriesRepository: CategoriesRepository = App.component.categoriesRepository()
    val categories: MutableLiveData<List<Category>> = categoriesRepository.getAll()
    val selectedCategory:  MutableLiveData<Category> = MutableLiveData()

    fun getAll(callback: CanReact? = null): MutableLiveData<List<Category>> {
        categoriesRepository.getAll(callback)
        return categories
    }
}