package com.piotrgluszek.announcementboard.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.adapters.AnnouncementListAdapter
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.dto.Category
import com.piotrgluszek.announcementboard.dto.SortingAndFilteringPreferences
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.utility.serialization.ByteArraySerializer
import com.piotrgluszek.announcementboard.viewmodel.AnnouncementsViewModel
import com.piotrgluszek.announcementboard.viewmodel.CategoriesViewModel
import com.piotrgluszek.announcementboard.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_board.*
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.piotrgluszek.announcementboard.auth.TokenStorage
import javax.inject.Inject


class Board : AppCompatActivity(), CategoriesFilteringDialog.CategoriesDialogListener {

    companion object {
        const val LOG_TAG = "BOARD_ACTIVITY"
        const val ID = "id"
        const val ACTION = "action"
        const val ACTION_UPDATE = "update"
        const val ACTION_CREATE = "create"
        const val CATEGORIES_DIALOG = "CATEGORIES_DIALOG"
    }

    @Inject
    lateinit var tokenStorage: TokenStorage
    private lateinit var announcementsViewModel: AnnouncementsViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: AnnouncementListAdapter
    private val sortingAndFilteringPreferences = SortingAndFilteringPreferences()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        App.component.inject(this)
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        adapter = AnnouncementListAdapter(this, R.layout.list_view_announcements, user = userViewModel.user.value)
        announcements_list.adapter = adapter

        announcementsViewModel = ViewModelProviders.of(this).get(AnnouncementsViewModel::class.java)
        announcementsViewModel.getAll(sortingAndFilteringPreferences).observe(
            this, Observer<ArrayList<Announcement>> {
                it?.let {
                    adapter.setAnnouncements(it)
                    sync.animation.cancel()
                }
            }
        )

        categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel::class.java)
        categoriesViewModel.selectedCategory.observe(this, Observer<Category> {
            categories.text = it?.name ?: "ALL"
        })

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
        announcements_list.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this@Board, SingleAnnouncement::class.java)
            intent.putExtra("id", position)
            startActivity(intent)
        }

        views_sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val sortingDirectionItem = views_sort.selectedItem as String
                var direction: String? = null
                when (sortingDirectionItem) {
                    ("ascending") -> direction = "asc"
                    ("descending") -> direction = "desc"
                }
                sortingAndFilteringPreferences.sortingOptions["views"] = direction
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        date_sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val sortingDirectionItem = date_sort.selectedItem as String
                var direction: String? = null
                when (sortingDirectionItem) {
                    ("ascending") -> direction = "asc"
                    ("descending") -> direction = "desc"
                }
                sortingAndFilteringPreferences.sortingOptions["date"] = direction
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }



        sortingAndFilteringPreferences.isFirst.observe(this, Observer<Boolean> {
            it?.let{
                if(it) prev_page.visibility = View.GONE
                else prev_page.visibility = View.VISIBLE
            }
        })
        sortingAndFilteringPreferences.isLast.observe(this, Observer<Boolean> {
            it?.let{
                if(it) next_page.visibility = View.GONE
                else next_page.visibility = View.VISIBLE
            }
        })

        next_page.setOnClickListener{
            sync.animation.start()
            sortingAndFilteringPreferences.number += 1
            announcementsViewModel.getAll(sortingAndFilteringPreferences)
            announcements_list.smoothScrollToPosition(0)
        }
        prev_page.setOnClickListener{
            sync.animation.start()
            sortingAndFilteringPreferences.number -= 1
            announcementsViewModel.getAll(sortingAndFilteringPreferences)
            announcements_list.smoothScrollToPosition(0)
        }


        val rotate = RotateAnimation(
            360f, 0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 4000
        rotate.repeatCount = Animation.INFINITE
        rotate.interpolator = LinearInterpolator()
        sync.animation = rotate

        sync.setOnClickListener{
            it.animation.start()
            announcementsViewModel.getAll(sortingAndFilteringPreferences)
        }

        log_out.setOnClickListener{
            tokenStorage.remove()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        if(userViewModel.user.value == null)
            logged_in_user_pane.visibility = View.GONE
    }

    fun onListItemEdit(id: Long) {
        val intent = Intent(this, EditAnnouncementActivity::class.java)
        intent.putExtra(ID, id)
        intent.putExtra(ACTION, ACTION_UPDATE)
        startActivity(intent)
    }

    fun onListItemRemove(id: Long) {
        announcementsViewModel.delete(id)
    }

    override fun onCategoriesSelected(selectedCategory: Category?) {
        categories.text = "All"
        sortingAndFilteringPreferences.category = selectedCategory

        selectedCategory?.let {
            categoriesViewModel.selectedCategory.value = selectedCategory
            // When category filter apply result set may shrink thus, results must be presented from the 1st page (0 based indexing)
            sortingAndFilteringPreferences.number = 0
        }
    }

    override fun onBackPressed() {
        if(userViewModel.user.value == null){
            super.onBackPressed()
        }
    }
}
