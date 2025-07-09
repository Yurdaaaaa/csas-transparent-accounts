package com.zj.core.csastest.util.ext

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.content.res.TypedArrayUtils.getAttr
import androidx.recyclerview.widget.RecyclerView
import com.zj.csastest.core.R

val Configuration.isDarkMode: Boolean
    get() = uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

fun Window.setLightSystemBars(lightStatusBar: Boolean, lightNavBar: Boolean) {
    var visibility = decorView.systemUiVisibility
    if (lightStatusBar) {
        visibility = visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        visibility = visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }

    if (isOreo()) {
        if (lightNavBar) {
            visibility = visibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            visibility = visibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }
    decorView.systemUiVisibility = visibility
}

private fun isOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

inline fun bundleOf(factory: Bundle.() -> Unit) = Bundle().apply(factory)

inline fun MutableList<Any>.toPayloadFlags(): Int {
    var mergedFlags = 0
    // Payloads can be batched, to or them together for easier usage
    for (i in 0 until size) {
        mergedFlags = mergedFlags or (this[i] as Int)
    }
    return mergedFlags
}


inline fun MutableList<Any>.applyAll(applyPayload: (Int) -> Boolean): Boolean {
    val flags = toPayloadFlags()
    if (flags > 0) {
        if (applyPayload(flags)) {
            return true
        }
    }
    return false
}

inline fun ifHasFlag(flags: Int, flag: Int, body: () -> Unit): Boolean {
    return if (flags.hasFlag(flag)) {
        body()
        true
    } else {
        false
    }
}

inline fun RecyclerView.ViewHolder.ifPositionValid(body: (Int) -> Unit) {
    adapterPosition.let { pos -> if (isPositionValidInternal(pos)) body(pos) }
}

fun isPositionValidInternal(pos: Int) = pos != RecyclerView.NO_POSITION