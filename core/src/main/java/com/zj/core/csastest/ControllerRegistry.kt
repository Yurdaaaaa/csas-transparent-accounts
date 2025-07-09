package com.zj.core.csastest

import com.bluelinelabs.conductor.Controller

interface ControllerRegistry {
    fun transparentAccountListController(): Controller
}