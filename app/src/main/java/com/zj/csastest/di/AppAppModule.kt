package com.zj.csastest.di

import com.zj.core.csastest.ControllerRegistry
import com.zj.csastest.ControllerRegistryImpl
import dagger.Module
import dagger.Provides

@Module
object AppAppModule {

    @Provides
    @JvmStatic
    fun controllerRegistry(): ControllerRegistry = ControllerRegistryImpl()
}