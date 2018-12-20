package com.piotrgluszek.announcementboard.view

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.adapters.AnnouncementListAdapter
import com.piotrgluszek.announcementboard.communication.AnnouncementsApi
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.extenstions.toast
import com.piotrgluszek.announcementboard.image.ImageConverter
import com.piotrgluszek.announcementboard.injection.ApiComponent
import com.piotrgluszek.announcementboard.injection.App
import kotlinx.android.synthetic.main.activity_edit_announcement.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class EditAnnouncementActivity : AppCompatActivity() {
    companion object {
        const val PICK_PHOTO = 1
    }

    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var api: AnnouncementsApi
    lateinit var announcement: Announcement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_announcement)
        getApiComponent()?.inject(this)
        val serializedAnnouncement = intent.getStringExtra(AnnouncementListAdapter.JSON_ANNOUNCEMENT)
        announcement = deserializeAnnouncement(serializedAnnouncement)
        setView(announcement)
    }

    private fun getApiComponent(): ApiComponent? {
        return App.component
    }

    private fun deserializeAnnouncement(serializedAnnouncement: String): Announcement {
        return gson.fromJson(serializedAnnouncement, Announcement::class.java)
    }

    private fun setView(announcemet: Announcement) {
        if (announcemet.photo != null) photo.setImageBitmap(ImageConverter.fromBase64(announcemet.photo))
        val title = findViewById<EditText>(R.id.title)
        title.setText(announcemet.title)
        description.setText(announcemet.description)
        photo.setOnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, PICK_PHOTO)
        }
        save_btn.setOnClickListener {
            getViewData()
        }
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

        val updatedAnnouncement = Announcement(title = newTitle, description = newDescription, photo = newPhoto)
        api.updateAnnoncement(announcement.id!!, updatedAnnouncement).enqueue(object : Callback<Announcement> {
            override fun onFailure(call: Call<Announcement>, t: Throwable) {
                Log.e(Login.LOG_TAG, String.format(Login.REQ_FAIL, t.message), t)
                Toast.makeText(this@EditAnnouncementActivity, "Connection problem occurred", Toast.LENGTH_SHORT)
            }

            override fun onResponse(call: Call<Announcement>, response: Response<Announcement>) {
                when (response.code()) {
                    200 -> {
                        toast("Announcement updated")
                        finish()
//                        val intent = Intent(this@EditAnnouncementActivity, Board::class.java)
//                        startActivity(intent)
                    }
                    403 -> {
                        toast("You shouldn't be here")
                    }
                }
            }
        })

    }
}
