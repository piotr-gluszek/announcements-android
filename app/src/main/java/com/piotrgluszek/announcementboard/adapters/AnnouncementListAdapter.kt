package com.piotrgluszek.announcementboard.adapters

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.piotrgluszek.announcementboard.R
import com.piotrgluszek.announcementboard.dto.Announcement
import com.piotrgluszek.announcementboard.dto.User
import com.piotrgluszek.announcementboard.extenstions.formattedDateString
import com.piotrgluszek.announcementboard.image.ImageConverter
import com.piotrgluszek.announcementboard.view.Board


class AnnouncementListAdapter(
    val context: Context,
    val resource: Int,
    var dataSource: ArrayList<Announcement> = ArrayList(),
    val user: User?
) :
    BaseAdapter() {

    class ViewHolder(
        val photo: ImageView,
        val title: TextView,
        val date: TextView,
        val edit: ImageButton,
        val remove: ImageButton
    )

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

    fun setAnnouncements(announcements: ArrayList<Announcement>) {
        dataSource = announcements;
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = convertView ?: inflater.inflate(R.layout.list_view_announcements, parent, false)
        if (convertView == null) {
            val photo = rowView.findViewById(R.id.photo) as ImageView
            val title = rowView.findViewById(R.id.title) as TextView
            val date = rowView.findViewById(R.id.date) as TextView
            val edit = rowView.findViewById(R.id.edit_btn) as ImageButton
            val remove = rowView.findViewById(R.id.remove_btn) as ImageButton
            val holder = ViewHolder(photo, title, date, edit, remove)
            rowView.tag = holder
        }

        val announcement = getItem(position) as Announcement
        val viewHolder = rowView.tag as ViewHolder
        viewHolder.title.text = announcement.title
        viewHolder.date.text = announcement.date?.formattedDateString(context.resources.getString(R.string.dateFormat))
        if (announcement.photo != null) {
            val imgByteArray = Base64.decode(announcement.photo, Base64.DEFAULT)
            Glide
                .with(context)
                .asBitmap()
                .load(imgByteArray)
                .into(viewHolder.photo)
        } else viewHolder.photo.setImageResource(R.drawable.image_placeholder)

        if (user?.id == announcement.announcer?.id) {
            viewHolder.edit.setOnClickListener {
                announcement.id?.let {
                    (context as Board).onListItemEdit(it)
                }
            }

            viewHolder.remove.setOnClickListener {
                announcement.id?.let {
                    (context as Board).onListItemRemove(it)
                }
            }
            viewHolder.edit.visibility = View.VISIBLE
            viewHolder.remove.visibility = View.VISIBLE
        } else {
            viewHolder.edit.visibility = View.INVISIBLE
            viewHolder.remove.visibility = View.INVISIBLE
        }
        return rowView
    }


}