package com.zj.csastest

import com.bluelinelabs.conductor.Controller
import com.zj.core.csastest.ControllerRegistry
import com.zj.csastest.transparentaccountlist.TransparentAccountListController

class ControllerRegistryImpl : ControllerRegistry {
    override fun transparentAccountListController(): Controller = TransparentAccountListController()

    // todo account detail screen
    // todo pincode screen
}