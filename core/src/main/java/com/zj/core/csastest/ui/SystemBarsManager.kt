package com.zj.core.csastest.ui

import com.bluelinelabs.conductor.Controller

interface SystemBarsManager {
    fun setLightSystemBars(lightStatusBar: Boolean, lightNavBar: Boolean, caller: Controller)

    fun clear(caller: Controller)
}