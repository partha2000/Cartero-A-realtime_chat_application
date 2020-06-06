package com.example.realtime_chat_application.ui.home
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.realtime_chat_application.R
import com.example.realtime_chat_application.model.message

class MessageAdapter(
    context: Context?,
    resource: Int,
    objects: List<message?>?
) :
    ArrayAdapter<message?>(context!!, resource, objects as MutableList<message?>) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        if (convertView == null) {
            convertView = (context as Activity).layoutInflater.inflate(R.layout.message_card, parent, false)
        }

        val photoImageView = convertView!!.findViewById<View>(R.id.sent_image) as ImageView

        val messageTextView = convertView.findViewById<View>(R.id.message_data) as TextView

        val authorTextView = convertView.findViewById<View>(R.id.sender) as TextView

        val message: message? = getItem(position)

        val isPhoto = message?.photoUrl != null

        if (isPhoto) {
            messageTextView.visibility = View.GONE
            photoImageView.visibility = View.VISIBLE
            Glide.with(photoImageView.context)
                .load(message?.photoUrl)
                .into(photoImageView)
        } else {
            messageTextView.visibility = View.VISIBLE
            photoImageView.visibility = View.GONE
            messageTextView.setText(message?.text)
        }
        authorTextView.setText(message?.name)
        return convertView
    }
}