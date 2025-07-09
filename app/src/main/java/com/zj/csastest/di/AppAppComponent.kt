package com.zj.csastest.di

import android.app.Application
import android.content.Context
import com.zj.core.csastest.di.AppModule
import com.zj.core.csastest.di.AppScope
import com.zj.core.csastest.di.AppComponent
import dagger.BindsInstance
import dagger.Component
import io.reactivex.Scheduler

@AppScope
@Component(
    modules = [
        AppAppModule::class,
        AppModule::class
    ]
)
interface AppAppComponent : AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance app: Application,
            @BindsInstance scheduler: Scheduler
        ): AppAppComponent
    }
}