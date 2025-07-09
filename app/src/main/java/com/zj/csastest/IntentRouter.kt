package com.zj.csastest

import android.content.Intent
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.noAnimSetRoot
import com.zj.core.csastest.ControllerRegistry
import com.zj.core.csastest.util.LOG

class IntentRouter(private val router: Router, private val controllerRegistry: ControllerRegistry) {

    fun routeIntent(intent: Intent) {
        val uri = intent.data
        if (uri != null) {
            // if app is installed intent.data will be filled or action send from system, click on push etc..
            // handle non null uri
        } else {
            handleDefault()
        }
    }

    private fun handleDefault() {
        if (!router.hasRootController()) {
            LOG.d("handleDefault -> show transparentAccountList screen")
//            router.noAnimSetRoot(controllerRegistry.pinController()) // todo udelat pincode, kdyz prijdes do appky
            router.noAnimSetRoot(controllerRegistry.transparentAccountListController())
        }
    }
}