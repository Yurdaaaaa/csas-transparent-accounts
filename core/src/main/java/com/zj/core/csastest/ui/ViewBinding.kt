package com.zj.core.csastest.ui

import android.view.View
import com.zj.core.csastest.view.TintToolbar
import com.zj.csastest.core.R

open class ViewBinding(view: View) {
    val toolbar: TintToolbar? = view.findViewById(R.id.toolbar)
}