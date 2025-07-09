package com.zj.csastest.transparentaccountlist

import com.jakewharton.rxrelay2.BehaviorRelay
import com.zj.core.csastest.data.TransparentAccountRepository
import com.zj.core.csastest.data.model.TransparentAccount
import com.zj.core.csastest.net.TransparentAccountsNetworkManager
import com.zj.core.csastest.net.TransparentAccountsNetworkManager.*
import com.zj.core.csastest.ui.ViewModel
import com.zj.core.csastest.util.LOG
import com.zj.core.csastest.util.ext.accept
import com.zj.core.csastest.util.ext.asEvents
import com.zj.core.csastest.util.ext.pairOf
import com.zj.core.csastest.util.ext.throwingSubscribe
import com.zj.core.csastest.util.ext.tupleOf
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class TransparentAccountListViewModel(
    private val transparentAccountRepository: TransparentAccountRepository,
    private val transparentAccountsNetworkManager: TransparentAccountsNetworkManager,
    private val navigator: TransparentAccountListNavigator,
    private val mainThreadScheduler: Scheduler
) : ViewModel<Nothing>() {

    private val requester = Requester()

    private val accountListRelay = BehaviorRelay.create<List<TransparentAccount>>()
    val accountListObservable: Observable<List<TransparentAccount>>
        get() = accountListRelay
    val accountListState: List<TransparentAccount>?
        get() = accountListRelay.value

    private val listLoaderStateRelay = BehaviorRelay.createDefault(ListLoaderState())
    val listLoaderStateObservable: Observable<ListLoaderState>
        get() = listLoaderStateRelay
    private val listLoaderState: ListLoaderState?
        get() = listLoaderStateRelay.value

    init {
        startObservingDbTransparentAccounts()
        displayFetchedDataInDb() // triggers data for offline or already saved accounts when page is loading so user has some data visible
        startObservingServerRequests()
    }

    private fun startObservingDbTransparentAccounts() {
        disposables += Observables
            .combineLatest(
                transparentAccountRepository.transparentAccountsChangedObservable(),
                listLoaderStateObservable // this will gets triggered even in offline
                    .filter {
                        it.requestEvent !is TransparentAccountsListEvent.Idle ||
                        it.requestEvent !is TransparentAccountsListEvent.Started
                    },
                requester.observable.distinctUntilChanged { prev, curr ->
                    println("TransparentAccountListViewModel db requester distinctUntilChanged - query is same: ${prev.query == curr.query} page is same: ${curr.page == prev.page}")
                    if (curr.force) false else (prev.query == curr.query && curr.page == prev.page)
                },
            ) { _, loadingState, request -> pairOf(loadingState, request) }
            .switchMap { pair ->
                val loaderState = pair.first
                val request = pair.second

                val data = if (request.query.isEmpty()){
                    transparentAccountRepository.getAllTransparentAccountsSorted()
                } else {
                    Observable.empty() // todo search
                }

                data.map { list -> tupleOf(request, list, loaderState) }
            }
            .filter { (request, dbList, loaderState) ->
                loaderState.requestEvent is TransparentAccountsListEvent.Success ||
                loaderState.requestEvent is TransparentAccountsListEvent.DefaultSuccess
            }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(mainThreadScheduler)
            .throwingSubscribe{ (request, dbList, loaderState) ->
                println("TransparentAccountListViewModel getAllTransparentAccountsSorted for page: ${request.page} query:${request.query} count: ${dbList.count()} loaderState: $loaderState")
                accountListRelay.accept(dbList)
            }
    }

    private fun startObservingServerRequests() {
        disposables += requester.observable
            .distinctUntilChanged { prev, curr ->
                println("TransparentAccountListViewModel requester distinctUntilChanged - query is same: ${prev.query == curr.query} page is same: ${curr.page == prev.page}")
                if (curr.force) false else (prev.query == curr.query && curr.page == prev.page)
            }
            .switchMap { request ->
                callTransparentAccountPageRequest(request)
                    .map { response ->
                        val isOfflineDefaultSuccess = response is TransparentAccountsListEvent.DefaultSuccess

                        Triple(request, response.hasMorePages, isOfflineDefaultSuccess)
                    }
                    .asEvents(
                        started = {
                            TransparentAccountsListEvent.Started(request.page, request.force, request.query.isNotEmpty())
                        },
                        success = { (request, hasMorePages, allDefaultSuccess) ->
                            if (allDefaultSuccess) {
                                TransparentAccountsListEvent.DefaultSuccess(request.page, request.force, request.query.isNotEmpty(), hasMorePages)
                            } else {
                                TransparentAccountsListEvent.Success(request.page, request.force, request.query.isNotEmpty(), hasMorePages)
                            }
                        },
                        errorr = { error ->
                            TransparentAccountsListEvent.Error(request.page, request.force, request.query.isNotEmpty(), error)
                        }
                    )
            }
            .observeOn(mainThreadScheduler)
            .throwingSubscribe { event ->
                when (event) {
                    is TransparentAccountsListEvent.Started -> {
                        listLoaderStateRelay.accept {
                            it.copy(requestEvent = event, page = event.page)
                        }
                    }
                    is TransparentAccountsListEvent.Success, is TransparentAccountsListEvent.DefaultSuccess -> {
                        listLoaderStateRelay.accept {
                            it.copy(requestEvent = event, page = event.page, hasMorePages = event.hasMorePages)
                        }
                    }
                    is TransparentAccountsListEvent.Error -> {
                        listLoaderStateRelay.accept {
                            it.copy(requestEvent = event)
                        }
                    }
                    TransparentAccountsListEvent.Idle -> {
                        // idle do nothing
                    }
                }
            }
    }

    private fun callTransparentAccountPageRequest(
        request: TransparentAccountsListRequest
    ): Observable<TransparentAccountsListEvent> {
        return transparentAccountsNetworkManager.getTransparentAccountList(request)
    }

    fun onAccountClicked(accountNumber: String) {
        navigator.goToDetail(accountNumber)
    }

    private fun displayFetchedDataInDb() {
        listLoaderStateRelay.accept {
            it.copy(requestEvent = TransparentAccountsListEvent.DefaultSuccess(0, true, false, true), page = 0, hasMorePages = true)
        }
    }

    fun refresh() {
        requester.set {
            it.copy(page = 0, query = "", force = true)
        }
    }

    fun loadNextPage() {
        LOG.d("calling loadNextPage")
        val state = listLoaderState

        if (state == null) {
            LOG.e("loadNextPage state is null")
            return
        }

        val requestEvent = state.requestEvent
        if (requestEvent is TransparentAccountsListEvent.Success && state.hasMorePages) {
            LOG.d("calling loadNextPage state.page: " + state.page)
            requester.set {
                val _query = if (it.query.length > 100) it.query.substring(0, 100) else it.query
                it.copy(query = _query.trim(), page = it.page + 1, force = false)
            }
        } else if (requestEvent is TransparentAccountsListEvent.DefaultSuccess){ // handle has more db pages?
            LOG.d("calling loadNextPage followed from database state.page: " + state.page)
            requester.set {
                val _query = if (it.query.length > 100) it.query.substring(0, 100) else it.query
                it.copy(query = _query.trim(), page = it.page + 1, force = false)
            }
        } else {
            LOG.d("event started but no more pages !!!")
        }
    }

    fun clearDb() {
        transparentAccountRepository.clear()
    }
}

class Requester() {

    private val relay = BehaviorRelay.createDefault(TransparentAccountsListRequest(page = 0, query = "", force = false)) // paging starts from index 0
    val observable: Observable<TransparentAccountsListRequest>
        get() = relay

    val getLastRequest: TransparentAccountsListRequest
        get() = relay.value!!

    fun set(reduce: (TransparentAccountsListRequest) -> TransparentAccountsListRequest) {
        relay.accept { reduce(it) }
    }
}

data class ListLoaderState(
    val page: Int = 0,
    val requestEvent: TransparentAccountsListEvent = TransparentAccountsListEvent.Idle,
    val hasMorePages: Boolean = true
)