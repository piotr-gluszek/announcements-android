package com.piotrgluszek.announcementboard.adapters

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.extenstions.formattedDateString
import com.piotrgluszek.announcementboard.extenstions.toast
import com.piotrgluszek.announcementboard.image.ImageConverter
import com.piotrgluszek.announcementboard.injection.ApiComponent
import com.piotrgluszek.announcementboard.injection.ApiModule
import com.piotrgluszek.announcementboard.injection.DaggerApiComponent
import com.piotrgluszek.announcementboard.view.EditAnnouncementActivity

class AnnouncementListAdapter(val context: Context, val resource: Int, val dataSource: ArrayList<Announcement>) :
    BaseAdapter() {

    companion object {
        const val JSON_ANNOUNCEMENT = "jsonSerializedAnnouncement"
        const val UPDATE_ANNOUNCEMENT = 1
    }

    private val gson: Gson = getApiComponent().gson()

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_view_announcements, parent, false)
        val photo = rowView.findViewById(R.id.photo) as ImageView
        val title = rowView.findViewById(R.id.title) as TextView
        val date = rowView.findViewById(R.id.date) as TextView
        val edit = rowView.findViewById(R.id.edit_btn) as ImageButton
        val remove = rowView.findViewById(R.id.remove_btn) as ImageButton

        val announcement = getItem(position) as Announcement
        title.text = announcement.title
        date.text = announcement.date.formattedDateString(context.resources.getString(R.string.dateFormat))
        if (announcement.photo != null) photo.setImageBitmap(ImageConverter.fromBase64(announcement.photo))
        else photo.setImageResource(R.drawable.image_placeholder)

        edit.setOnClickListener {
            val intent = Intent(context, EditAnnouncementActivity::class.java)
            intent.putExtra(JSON_ANNOUNCEMENT, gson.toJson(announcement))
            context.startActivity(intent)
        }

        remove.setOnClickListener {
            context.toast("TODO(Implement removing announcement)")
        }
        return rowView
    }

    private fun getApiComponent(): ApiComponent {
        return DaggerApiComponent.builder().apiModule(ApiModule(context.applicationContext as Application)).build()
    }
}