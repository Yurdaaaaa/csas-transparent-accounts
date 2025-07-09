package com.zj.csastest

import android.app.Application
import android.os.Looper
import com.zj.csastest.di.AppAppComponent
import com.jakewharton.threetenabp.AndroidThreeTen
import com.zj.core.csastest.util.LOG
import com.zj.core.csastest.di.AppComponent
import com.zj.core.csastest.di.AppComponentProvider
import com.zj.csastest.di.DaggerAppAppComponent
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins


class App : Application(), AppComponentProvider {

    private lateinit var _appComponent: AppAppComponent

    override val appComponent: AppComponent
        get() = _appComponent

    override fun onCreate() {
        super.onCreate()
        LOG.i("App # onCreate")

        AndroidThreeTen.init(this) // date & time API

        _appComponent = DaggerAppAppComponent.factory().create(
            app = this,
            context = this,
            scheduler = AndroidSchedulers.mainThread()
        )
    }

    companion object {
        init {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler {
                AndroidSchedulers.from(Looper.getMainLooper(), true)
            }
            RxJavaPlugins.setErrorHandler {
                // https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
                // Swallow UndeliverableException as these are exceptions
                // in source observables that have no subscribers, which is
                // fine since for example publish().autoConnect(), should not
                // care if it has subscribers or not
                // -- However, not sure why they didn't swallow this UndeliverableException
                // automatically, hmmm
                if (it is UndeliverableException) return@setErrorHandler
                throw it
            }
        }
    }
}