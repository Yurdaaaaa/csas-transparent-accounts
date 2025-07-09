package com.zj.csastest.transparentaccountlist.di

import com.zj.core.csastest.data.TransparentAccountRepository
import com.zj.core.csastest.di.AppComponent
import com.zj.core.csastest.net.TransparentAccountsNetworkManager
import com.zj.csastest.transparentaccountlist.TransparentAccountListController
import com.zj.csastest.transparentaccountlist.TransparentAccountListViewModel
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import javax.inject.Scope

@TransparentAccountListControllerScope
@Component(dependencies = [AppComponent::class], modules = [TransparentAccountListComponentModule::class])
internal interface TransparentAccountListControllerComponent {
    val transparentAccountListViewModel: TransparentAccountListViewModel

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent,
            @BindsInstance controller: TransparentAccountListController,
        ): TransparentAccountListControllerComponent
    }
}

@Module
object TransparentAccountListComponentModule {

    @JvmStatic
    @Provides
    fun viewModel(
        transparentAccountRepository: TransparentAccountRepository,
        transparentAccountsNetworkManager: TransparentAccountsNetworkManager,
        controller: TransparentAccountListController,
        mainThreadScheduler: Scheduler
    ) = TransparentAccountListViewModel(
        transparentAccountRepository,
        transparentAccountsNetworkManager,
        controller,
        mainThreadScheduler
    )
}

@Scope
@Retention(AnnotationRetention.SOURCE)
private annotation class TransparentAccountListControllerScope