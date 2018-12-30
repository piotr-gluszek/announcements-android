package com.piotrgluszek.announcementboard.view

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.widget.ArrayAdapter
import com.piotrgluszek.announcementboard.dto.Category
import com.piotrgluszek.announcementboard.enums.Action
import com.piotrgluszek.announcementboard.interfaces.CanReact
import com.piotrgluszek.announcementboard.utility.serialization.ByteArraySerializer
import com.piotrgluszek.announcementboard.viewmodel.CategoriesViewModel
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.lang.Thread.sleep


class CategoriesFilteringDialog : AppCompatDialogFragment(), CanReact {

    private lateinit var alertDialog: AlertDialog
    private lateinit var categories: List<Category>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Categories")
        val serializedCategoriesList = arguments.getByteArray("content")
        categories = ByteArraySerializer.deserialize(serializedCategoriesList) as List<Category>
        val categoryNames = categories?.map { category -> category.name }?.toMutableList()
        categoryNames?.add(0, "All")
        builder.setItems(categoryNames?.toTypedArray()) { _, which ->
            val parent = context as CategoriesDialogListener
            if (which != 0)
                parent.onCategoriesSelected(categories?.get(which - 1))
            else
                parent.onCategoriesSelected(null)
        }

        alertDialog = builder.create()
        return alertDialog
    }

    interface CategoriesDialogListener {
        fun onCategoriesSelected(category: Category?)
    }

    override fun onSuccess(action: Action, data: Any?) {
    }

    override fun onFail(action: Action, t: Throwable) {
    }
}