package com.piotrgluszek.announcementboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.extenstions.formattedDateString
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class AnnouncementListAdapter(val context: Context, val resource: Int, val dataSource: ArrayList<Announcement>): BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
       //photo
        return rowView
    }
}