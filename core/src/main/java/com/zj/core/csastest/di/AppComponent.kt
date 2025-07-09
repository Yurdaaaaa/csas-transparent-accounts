package com.zj.core.csastest.di

import android.content.Context
import com.zj.core.csastest.ControllerRegistry
import com.zj.core.csastest.data.TransparentAccountRepository
import com.zj.core.csastest.net.TransparentAccountsNetworkManager
import io.reactivex.Scheduler
import okhttp3.OkHttpClient

interface AppComponent {
    val context: Context
    val mainThreadScheduler: Scheduler
    @get:BaseOkHttp val baseOkHttpClient: OkHttpClient
    @get:CsasOkHttp val csasOkHttp: OkHttpClient
    val transparentAccountRepository: TransparentAccountRepository
    val transparentAccountsNetworkManager: TransparentAccountsNetworkManager
    val controllerRegistry: ControllerRegistry
}