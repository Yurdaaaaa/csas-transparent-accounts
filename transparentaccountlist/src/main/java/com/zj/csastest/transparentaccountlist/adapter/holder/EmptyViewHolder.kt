package com.zj.csastest.transparentaccountlist.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zj.csastest.transparentaccountlist.R

open class EmptyViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val icon: ImageView = view.findViewById(R.id.iconEmpty)
    private val title: TextView = view.findViewById(R.id.titleEmptyTextView)
    private val subtitle: TextView = view.findViewById(R.id.subtitleEmptyTextView)

    fun bind() {
        val iconColorState = ContextCompat.getColorStateList(itemView.context, R.color.black)
        icon.imageTintList = iconColorState

        title.setTextColor(itemView.context.getColor(R.color.black))
        subtitle.setTextColor(itemView.context.getColor(R.color.black))
    }
}