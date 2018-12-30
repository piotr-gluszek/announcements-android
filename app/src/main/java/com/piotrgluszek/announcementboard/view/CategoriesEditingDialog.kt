package com.piotrgluszek.announcementboard.view

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import com.piotrgluszek.announcementboard.dto.Category
import com.piotrgluszek.announcementboard.utility.serialization.ByteArraySerializer


class CategoriesEditingDialog : AppCompatDialogFragment() {
    private lateinit var allCategories: List<Category>
    private lateinit var selectedCategories: MutableList<Category>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Categories")
        val serializedAllCategoriesList = arguments.getByteArray("all_categories")
        allCategories = ByteArraySerializer.deserialize(serializedAllCategoriesList) as List<Category>

        val serializedSelectedCategoriesList = arguments.getByteArray("selected_categories")
        selectedCategories = ByteArraySerializer.deserialize(serializedSelectedCategoriesList) as? MutableList<Category> ?: mutableListOf<Category>()

        val categoryNames = allCategories?.map { category -> category.name }.toMutableList()
        var checkedItems = mutableListOf<Boolean>()
        for (category in allCategories) {
            if (selectedCategories.contains(category))
                checkedItems.add(true)
            else checkedItems.add(false)
        }

        builder.setMultiChoiceItems(
            categoryNames.toTypedArray(),
            checkedItems.toBooleanArray()
        ) { _, which, isChecked ->
            if (isChecked)
                selectedCategories.add(allCategories.filter { category -> category.name == categoryNames[which] }.first())
            else
                selectedCategories =
                        selectedCategories.filter { category -> category.name != categoryNames[which] }.toMutableList()
        }

        builder.setPositiveButton("OK") { _, _ ->
            val parent = context as CategoriesEditingDialogListener
            parent.onCategoriesSelected(selectedCategories)
        }
        return builder.create()
    }

    interface CategoriesEditingDialogListener {
        fun onCategoriesSelected(categories: MutableList<Category>?)
    }
}