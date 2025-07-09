package com.zj.csastest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.zj.core.csastest.di.AppComponentProvider
import com.zj.core.csastest.util.ext.isDarkMode
import com.zj.core.csastest.ui.SystemBarHelper
import com.zj.core.csastest.ui.SystemBarsManager

class MainActivity : AppCompatActivity(), SystemBarsManager {

    private val appComponent
        get() = (application as AppComponentProvider).appComponent

    private lateinit var router: Router
    private var pinCodeRouter: Router? = null

    private lateinit var systemBarHelper: SystemBarHelper
    private lateinit var intentRouter: IntentRouter

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentRouter.routeIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val window = window

        systemBarHelper = SystemBarHelper(window, resources.configuration.isDarkMode) { it == pinCodeRouter }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        router = Conductor.attachRouter(
            this, findViewById(R.id.controllerContainer), savedInstanceState
        )

        intentRouter = IntentRouter(router, appComponent.controllerRegistry)
        intentRouter.routeIntent(intent)

        supportActionBar?.hide()
    }

    override fun setLightSystemBars(
        lightStatusBar: Boolean,
        lightNavBar: Boolean,
        caller: Controller
    ) {
        systemBarHelper.setLightSystemBars(lightStatusBar, lightNavBar, caller)
    }


    override fun clear(caller: Controller) {
        systemBarHelper.clear(caller)
    }
}