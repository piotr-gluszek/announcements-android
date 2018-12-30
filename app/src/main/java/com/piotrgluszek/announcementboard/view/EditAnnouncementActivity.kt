package com.piotrgluszek.announcementboard.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import br.com.ilhasoft.support.validation.Validator
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.databinding.ActivityEditAnnouncementBinding
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.dto.Category
import com.piotrgluszek.announcementboard.image.ImageConverter
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.utility.serialization.ByteArraySerializer
import com.piotrgluszek.announcementboard.viewmodel.AnnouncementsViewModel
import com.piotrgluszek.announcementboard.viewmodel.CategoriesViewModel
import com.piotrgluszek.announcementboard.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_edit_announcement.*


class EditAnnouncementActivity : AppCompatActivity(), CategoriesEditingDialog.CategoriesEditingDialogListener {
    companion object {
        const val PICK_PHOTO = 1
    }

    lateinit var announcementViewModel: AnnouncementsViewModel
    lateinit var userViewModel: UserViewModel
    lateinit var categoriesViewModel: CategoriesViewModel
    var id: Long = 0
    lateinit var validator: Validator
    lateinit var action: String
    lateinit var currentAnnouncement: Announcement
    lateinit var unsavedModifiedAnnouncement: Announcement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_announcement)
        val binding: ActivityEditAnnouncementBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_edit_announcement)
        validator = Validator(binding)
        validator.enableFormValidationMode()
        App.component.inject(this)
        announcementViewModel = ViewModelProviders.of(this).get(AnnouncementsViewModel::class.java)
        action = intent.getStringExtra(Board.ACTION)
        if (action == Board.ACTION_UPDATE) {
            id = intent.getLongExtra("id", 0)
            announcementViewModel.announcements.value?.let {
                it.filter { a -> a.id == id }.first()?.apply {
                    currentAnnouncement = this
                    unsavedModifiedAnnouncement = this.copy()
                    setView(this)
                }
            }
        } else {
            unsavedModifiedAnnouncement = Announcement()
        }
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel::class.java)

        photo.setOnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, PICK_PHOTO)
        }
        save_btn.setOnClickListener {
            getViewData()
            if (validator.validate()) {
                when (action) {
                    Board.ACTION_UPDATE -> {
                        announcementViewModel.update(id, unsavedModifiedAnnouncement)
                        currentAnnouncement.apply {
                            this.description = unsavedModifiedAnnouncement.description
                            this.title = unsavedModifiedAnnouncement.title
                            this.categories = unsavedModifiedAnnouncement.categories
                            this.photo = unsavedModifiedAnnouncement.photo
                        }
                    }
                    Board.ACTION_CREATE -> announcementViewModel.create(unsavedModifiedAnnouncement)
                }
                finish()
            }
        }

        edit_categories.setOnClickListener {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
            val bundle = Bundle()
            val selectedCategories =
                ByteArraySerializer.serialize(unsavedModifiedAnnouncement.categories ?: mutableListOf<Category>())
            val allCategories =
                ByteArraySerializer.serialize(categoriesViewModel.categories.value ?: listOf<Category>())
            bundle.putByteArray("all_categories", allCategories)
            bundle.putByteArray("selected_categories", selectedCategories)
            CategoriesEditingDialog().apply {
                arguments = bundle
                show(supportFragmentManager, "EDIT_CATEGORIES")
            }
        }
    }

    private fun setView(announcement: Announcement) {
        announcement.photo?.let {
            photo.setImageBitmap(ImageConverter.fromBase64(announcement.photo as String))
        }
        val title = findViewById<EditText>(R.id.title)
        title.setText(announcement.title)
        description.setText(announcement.description)
        setCategoriesToTextView(announcement.categories)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_PHOTO && resultCode == RESULT_OK) {
            val imageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            photo.setImageBitmap(bitmap)
        }
    }

    private fun getViewData() {
        val bitmap = (photo.drawable as? BitmapDrawable)?.bitmap
        var newPhoto: String? = null
        if (bitmap != null)
            newPhoto = ImageConverter.toBase64(bitmap)
        val title = findViewById<EditText>(R.id.title)
        val newTitle = title.text.toString()
        val newDescription = description.text.toString()
        val announcer = userViewModel.user.value
        unsavedModifiedAnnouncement.apply {
            this.title = newTitle
            this.description = newDescription
            this.photo = newPhoto
            this.announcer = announcer
        }
    }

    override fun onCategoriesSelected(categories: MutableList<Category>?) {
        setCategoriesToTextView(categories)
        unsavedModifiedAnnouncement.categories = categories
    }

    private fun setCategoriesToTextView(categories: MutableList<Category>?){
        var categoriesAsText = ""
        categories?.let {
            for (category in it) {
                categoriesAsText += (category.name + "\n")
            }
        }
        category_names.text = categoriesAsText
    }
}
