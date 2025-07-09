package com.zj.core.csastest.di

import dagger.Module

@Module(
    includes = [
        NetModule::class,
        DataModule::class
    ]
)
class AppModule