package com.zj.csastest.transparentaccountlist

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.zj.core.csastest.data.mapper.TransparentAccountItemMapper
import com.zj.core.csastest.di.AppComponent
import com.zj.core.csastest.net.TransparentAccountsNetworkManager
import com.zj.core.csastest.net.TransparentAccountsNetworkManager.TransparentAccountsListEvent
import com.zj.core.csastest.ui.BaseController
import com.zj.core.csastest.ui.ViewBinding
import com.zj.core.csastest.util.LOG
import com.zj.core.csastest.util.ListAccountsItemScrollListener
import com.zj.core.csastest.util.ext.bundleOf
import com.zj.core.csastest.util.ext.isDarkMode
import com.zj.core.csastest.util.ext.pairOf
import com.zj.core.csastest.util.ext.throwingSubscribe
import com.zj.core.csastest.util.ext.tupleOf
import com.zj.core.csastest.view.TintToolbar
import com.zj.csastest.transparentaccountlist.adapter.ITransparentAccountAdapter
import com.zj.csastest.transparentaccountlist.adapter.TransparentAccountAdapter
import com.zj.csastest.transparentaccountlist.adapter.TransparentAccountClickListener
import com.zj.csastest.transparentaccountlist.di.DaggerTransparentAccountListControllerComponent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class TransparentAccountListController : BaseController<TransparentAccountListViewModel, TransparentAccountListBinding, Nothing>, TransparentAccountListNavigator {

    constructor() : this(bundleOf {
    })

    constructor(bundle: Bundle) : super(bundle)

    override fun hasLightStatusBar() = true
    override fun hasLightNavBar() = true
    override fun hasUpButton() = false

    override fun onCreateViewModel(
        appComponent: AppComponent,
        savedState: Nothing?
    ): TransparentAccountListViewModel {

        return DaggerTransparentAccountListControllerComponent.factory()
            .create(
                appComponent = appComponent,
                controller = this
            )
            .transparentAccountListViewModel
    }

    override fun onSetupToolbar(toolbar: TintToolbar) {
        super.onSetupToolbar(toolbar)
        val context = toolbar.context

        toolbar.inflateMenu(R.menu.menu_accounts_transparent)
        toolbar.setTitle(context.getString(R.string.transparent_accounts_title))
        if (appComponent.context.resources.configuration.isDarkMode) {
            toolbar.setTitleTextColor(context.getColor(com.zj.csastest.core.R.color.white))
        } else {
            toolbar.setTitleTextColor(context.getColor(com.zj.csastest.core.R.color.black))
        }

        // FIXME: for some reason tint on three dot icon doesnt work
    }

    override fun handleMenuItemClicked(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_db -> {
                viewModel.clearDb()
                true
            }
            else -> super.handleMenuItemClicked(item)
        }
    }

    override fun onViewCreated(
        activity: Activity,
        viewBinding: TransparentAccountListBinding,
        viewModel: TransparentAccountListViewModel,
        savedViewState: Bundle?
    ) {
        createTransparentAccountsAdapter(activity, viewBinding)

        viewBinding.let {
            it.swipeRefreshLayout.setOnRefreshListener {
                viewModel.refresh()
            }
        }
    }

    private fun createTransparentAccountsAdapter(activity: Activity, viewBinding: TransparentAccountListBinding) {
        viewBinding.recyclerViewTransparentAccounts.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter =  TransparentAccountAdapter(activity, object : TransparentAccountClickListener {
                override fun onAccountClicked(accountNumber: String) {
                    viewModel.onAccountClicked(accountNumber)
                }
            })

            object : ListAccountsItemScrollListener() {
                override fun onScrolledToEnd() {
                    (viewBinding.recyclerViewTransparentAccounts.adapter as? ITransparentAccountAdapter<*>)?.let { adapter ->
                        LOG.d("TransparentAccountListController recyclerViewTransparentAccounts isNotEmpty:${adapter.isNotEmpty()}")

                        if (adapter.isNotEmpty()) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            }.attachToRecyclerView(this, null)
        }
    }

    override fun onViewAttached(
        activity: Activity,
        viewBinding: TransparentAccountListBinding,
        viewModel: TransparentAccountListViewModel,
        disposables: CompositeDisposable
    ) {
        val accountListObservableShared = viewModel.accountListObservable.share()
        val listLoaderStateObservableShared = viewModel.listLoaderStateObservable.share()

        val adapter = viewBinding.recyclerViewTransparentAccounts.adapter as ITransparentAccountAdapter<*>

        disposables += Observables
            .combineLatest(
                listLoaderStateObservableShared,
                accountListObservableShared
            )
            { loadingState, accountListState -> pairOf(loadingState, accountListState) }
            .distinctUntilChanged()
            .flatMap { (loadingState, accountListState) ->
                Observable.fromCallable {
                    val shouldAddProgress = loadingState.requestEvent is TransparentAccountsListEvent.Started
                    val listItems = TransparentAccountItemMapper.mapAccounts(accountListState, shouldAddProgress, false, true)

                    tupleOf(loadingState, listItems)
                }.subscribeOn(Schedulers.computation())
            }
            .subscribeOn(Schedulers.io())
            .observeOn(appComponent.mainThreadScheduler)
            .throwingSubscribe { (loadingState, accountListItems) ->
                LOG.d("accountListItems size: ${accountListItems.size}")
                adapter.setData(accountListItems,
                    preCalculate = {
                        0
                    }
                ) { diff, scroll ->

                }

                when (loadingState.requestEvent) {
                    is TransparentAccountsListEvent.Started -> {
                        LOG.d("TransparentAccountsListEvent started")
                    }
                    is TransparentAccountsListEvent.Success, is TransparentAccountsListEvent.DefaultSuccess -> {
                        LOG.d("TransparentAccountsListEvent Success")
                        viewBinding.apply {
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                    is TransparentAccountsListEvent.Error -> {
                        LOG.d("TransparentAccountsListEvent Error ${loadingState.requestEvent.error}")
                        viewBinding.apply {
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
    }

    override fun layoutRes(): Int = R.layout.controller_transparent_account_list

    override fun onCreateViewBinding(view: View): TransparentAccountListBinding = TransparentAccountListBinding(view)

    override fun goToDetail(accountNumber: String) {
        // todo open account detail screen in another module not implemented
    }
}

class TransparentAccountListBinding(view: View) : ViewBinding(view) {
    val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeToRefreshLayout)
    val recyclerViewTransparentAccounts: RecyclerView = view.findViewById(R.id.recyclerViewTransparentAccounts)
}

interface TransparentAccountListNavigator {
    fun goToDetail(accountNumber: String)
}