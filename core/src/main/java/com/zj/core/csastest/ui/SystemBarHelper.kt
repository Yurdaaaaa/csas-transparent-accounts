package com.zj.core.csastest.ui

import android.view.Window
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.zj.core.csastest.util.ext.setLightSystemBars

class SystemBarHelper(
    private val window: Window,
    private val isDarkMode: Boolean,
    private val isPriority: (Router) -> Boolean
) {
    private var prioritySet: Boolean = false

    private var priorityHasLightStatusBar = false
    private var priorityHasLightNavBar = false
    private var hasLightStatusBar = false
    private var hasLightNavBar = false

    fun setLightSystemBars(
        lightStatusBar: Boolean,
        lightNavBar: Boolean,
        caller: Controller
    ) {
        val priorityController = isPriority(caller.router)
        if (priorityController) {
            priorityHasLightStatusBar = lightStatusBar
            priorityHasLightNavBar = lightNavBar

            prioritySet = true
            setLightSystemBars(lightStatusBar, lightNavBar)
        } else {
            hasLightStatusBar = lightStatusBar
            hasLightNavBar = lightNavBar

            // If has priority controller, just save the values, they will be reflected
            // once priority controller is popped
            if (!prioritySet) {
                setLightSystemBars(lightStatusBar, lightNavBar)
            }
        }
    }

    fun clear(caller: Controller) {
        if (isPriority(caller.router)) {
            prioritySet = false
            priorityHasLightStatusBar = false
            priorityHasLightNavBar = false

            setLightSystemBars(hasLightStatusBar, hasLightNavBar)
        }
    }

    private fun setLightSystemBars(lightStatusBar: Boolean, lightNavBar: Boolean) {
        if (isDarkMode) {
            window.setLightSystemBars(false, false)
        } else {
            window.setLightSystemBars(lightStatusBar, lightNavBar)
        }
    }
}