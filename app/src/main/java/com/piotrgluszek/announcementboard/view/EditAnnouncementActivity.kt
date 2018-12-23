package com.piotrgluszek.announcementboard.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import com.google.gson.Gson
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.image.ImageConverter
import com.piotrgluszek.announcementboard.injection.App
import com.piotrgluszek.announcementboard.viewmodel.AnnouncementsViewModel
import kotlinx.android.synthetic.main.activity_edit_announcement.*
import javax.inject.Inject


class EditAnnouncementActivity : AppCompatActivity() {
    companion object {
        const val ANNOUNCEMENT_JSON = "jsonSerializedAnnouncement"
        const val PICK_PHOTO = 1
    }

    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var api: AnnouncementsApi
    lateinit var announcementViewModel: AnnouncementsViewModel
    var id: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_announcement)
        App.component.inject(this)
        if (intent.getStringExtra(Board.ACTION) == Board.ACTION_UPDATE) {
            id = intent.getLongExtra("id", 0)
            announcementViewModel = ViewModelProviders.of(this).get(AnnouncementsViewModel::class.java)
            announcementViewModel.announcements.value?.let {
                it.filter { a -> a.id == id }.first()?.let {
                    setView(it)
                }
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
        photo.setOnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, PICK_PHOTO)
        }
        save_btn.setOnClickListener {
            val announcement = getViewData()
            announcementViewModel.update(id, announcement)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_PHOTO && resultCode == RESULT_OK) {
            val imageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            photo.setImageBitmap(bitmap)
        }
    }

    private fun getViewData(): Announcement {
        val bitmap = (photo.drawable as? BitmapDrawable)?.bitmap
        var newPhoto: String? = null
        if (bitmap != null)
            newPhoto = ImageConverter.toBase64(bitmap)
        val title = findViewById<EditText>(R.id.title)
        val newTitle = title.text.toString()
        val newDescription = description.text.toString()

        return Announcement(title = newTitle, description = newDescription, photo = newPhoto)
    }
}
