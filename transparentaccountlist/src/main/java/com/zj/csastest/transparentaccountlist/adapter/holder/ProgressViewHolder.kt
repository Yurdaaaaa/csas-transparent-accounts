package com.zj.csastest.transparentaccountlist.adapter.holder

import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zj.csastest.transparentaccountlist.R

open class ProgressViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

    init {
        val iconColorState = ContextCompat.getColorStateList(itemView.context, R.color.blue_george)
        progressBar.progressTintList = iconColorState
    }
}