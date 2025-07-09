package com.zj.core.csastest.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.Toolbar
import com.zj.csastest.core.R

class TintToolbar : Toolbar {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    val isShowing: Boolean
        get() = translationY >= 0
    var menuInvalidatedListener: ((Menu) -> Unit)? = null

    fun show() {
        visibility = View.VISIBLE
        animate().translationY(0f)
            .alpha(1f)
            .setDuration(SLIDE_DURATION)
            .setInterpolator(sInterpolator)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.VISIBLE
                }
            })
    }

    fun hide() {
        visibility = View.VISIBLE
        animate().translationY((-measuredHeight).toFloat())
            .alpha(0f)
            .setDuration(SLIDE_DURATION)
            .setInterpolator(sInterpolator)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.INVISIBLE
                }
            })
    }

    fun showImmediate() {
        translationY = 0f
        alpha = 1f
        visibility = View.VISIBLE
    }

    fun hideImmediate() {
        if (measuredHeight > 0) {
            doHideImmediate()
        } else {
            post { doHideImmediate() }
        }
    }

    private fun doHideImmediate() {
        translationY = (-measuredHeight).toFloat()
        alpha = 0f
        visibility = View.INVISIBLE
    }

    override fun setNavigationIcon(icon: Drawable?) {
        super.setNavigationIcon(icon?.mutate()?.apply { setTint(context.getColor(R.color.color_primary)) })
    }

    override fun inflateMenu(resId: Int) {
        super.inflateMenu(resId)

        val menu = menu
        menuInvalidatedListener?.invoke(menu)

        colorMenu(menu, context.getColor(R.color.color_primary))
    }

    override fun invalidateMenu() {
        menuInvalidatedListener?.invoke(menu)
        colorMenu(menu, context.getColor(R.color.color_primary))
    }

    private fun colorMenu(menu: Menu, color: Int) {
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            val d = menuItem.icon
            if (d != null) {
                menuItem.icon = d.mutate().apply { setTint(color) }
            }
        }
    }

    companion object {
        private const val SLIDE_DURATION: Long = 200
        private val sInterpolator = DecelerateInterpolator()
    }
}
