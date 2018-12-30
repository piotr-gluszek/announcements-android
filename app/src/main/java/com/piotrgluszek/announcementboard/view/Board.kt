package com.piotrgluszek.announcementboard.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.adapters.AnnouncementListAdapter
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.dto.Category
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.utility.serialization.ByteArraySerializer
import com.piotrgluszek.announcementboard.viewmodel.AnnouncementsViewModel
import com.piotrgluszek.announcementboard.viewmodel.CategoriesViewModel
import com.piotrgluszek.announcementboard.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_board.*


class Board : AppCompatActivity(), CategoriesFilteringDialog.CategoriesDialogListener {

    companion object {
        const val LOG_TAG = "BOARD_ACTIVITY"
        const val ID = "id"
        const val ACTION = "action"
        const val ACTION_UPDATE = "update"
        const val ACTION_CREATE = "create"
        const val CATEGORIES_DIALOG = "CATEGORIES_DIALOG"
    }

    private lateinit var announcementsViewModel: AnnouncementsViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        App.component.inject(this)
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        val adapter = AnnouncementListAdapter(this, R.layout.list_view_announcements, user = userViewModel.user.value)
        announcements_list.adapter = adapter

        announcementsViewModel = ViewModelProviders.of(this).get(AnnouncementsViewModel::class.java)
        announcementsViewModel.announcements.observe(
            this, Observer<ArrayList<Announcement>> {
                it?.let {
                    adapter.setAnnouncements(it)
                }
            }
        )

        categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel::class.java)
        categoriesViewModel.selectedCategory.observe(this, Observer<Category>{
            categories.text = it?.name ?: "ALL"
        } )

        categories.setOnClickListener {
            val categories = categoriesViewModel.categories.value ?: mutableListOf()
            val bundle = Bundle()
            val serializedCategories = ByteArraySerializer.serialize(categories)
            bundle.putByteArray("content", serializedCategories)
            val dialog = CategoriesFilteringDialog()
                dialog.arguments = bundle
                dialog.show(supportFragmentManager, CATEGORIES_DIALOG)
        }

        addNew.setOnClickListener {
            val intent = Intent(this, EditAnnouncementActivity::class.java)
            intent.putExtra(ACTION, ACTION_CREATE)
            startActivity(intent)
        }
    }

    fun onListItemEdit(id: Long) {
        val intent = Intent(this, EditAnnouncementActivity::class.java)
        intent.putExtra(ID, id)
        intent.putExtra(ACTION, ACTION_UPDATE)
        startActivity(intent)
    }

    fun onListItemRemove() {

    }

    override fun onCategoriesSelected(selectedCategory: Category?) {
        categories.text = "All"
        selectedCategory?.let{
            categoriesViewModel.selectedCategory.value = selectedCategory
//            categories.text = it.name
        }
    }
}
