package com.zj.core.csastest.net

import com.zj.core.csastest.data.TransparentAccountRepository
import com.zj.core.csastest.data.mapper.ApiToDbTransparentAccountMapper
import com.zj.core.csastest.net.model.api.TransparentAccountsListResponse
import com.zj.core.csastest.util.LOG
import com.zj.core.csastest.util.ext.asEvents
import io.reactivex.Observable
import io.reactivex.Scheduler

class TransparentAccountsNetworkManager(
    private val transparentAccountsApiClient: TransparentAccountsApiClient,
    private val transparentAccountRepository: TransparentAccountRepository,
    private val apiToDbTransparentAccountMapper: ApiToDbTransparentAccountMapper,
    private val ioThreadScheduler: Scheduler
) {

    fun getTransparentAccountList(request: TransparentAccountsListRequest): Observable<TransparentAccountsListEvent> {
        return transparentAccountsApiClient.getTransparentAccountList(
            request.page
        )
            .onErrorResumeNext(
                Observable.just(TransparentAccountsListResponse(pageNumber = request.page, pageSize = 0, pageCount = 0, nextPage = request.page, recordCount = 0, isDefaultOfflineSuccessResponse = true))
            )
            .flatMap { response ->
                Observable.fromCallable {
                    val accounts = response.accounts
                    val dbTransparentAccounts = accounts.map { apiAccount ->
                        apiToDbTransparentAccountMapper.mapApiAccountToDbAccount(apiAccount)
                    }

                    try {
                        transparentAccountRepository.insertOrUpdateTransparentAccounts(dbTransparentAccounts)
                    } catch (e: Exception) {
                        LOG.e("TransparentAccountsNetworkManager insertOrUpdateTransparentAccounts ${e.printStackTrace()}")
                    }

                    response
                }.subscribeOn(ioThreadScheduler)
            }
            .asEvents(
                started = {
                    TransparentAccountsListEvent.Started(
                        page = request.page,
                        force = request.force,
                        isFiltered = request.query.isNotEmpty()
                    )
                },
                success = {
                    val hasMorePages = it.pageCount > it.pageNumber // paging is param is broken for some reason so i cannot test this properly

                    if (it.isDefaultOfflineSuccessResponse) {
                        TransparentAccountsListEvent.DefaultSuccess(
                            page = request.page,
                            force = request.force,
                            isFiltered = request.query.isNotEmpty(),
                            hasMorePages = true
                        )
                    } else {
                        TransparentAccountsListEvent.Success(
                            page = request.page,
                            force = request.force,
                            isFiltered = request.query.isNotEmpty(),
                            hasMorePages = hasMorePages
                        )
                    }
                },
                errorr = {
                    TransparentAccountsListEvent.Error(
                        page = request.page,
                        force = request.force,
                        isFiltered = request.query.isNotEmpty(),
                        error = it
                    )
                }
            )
            .subscribeOn(ioThreadScheduler)
    }

    data class TransparentAccountsListRequest(
        val page: Int,
        val query: String,
        val force: Boolean
    )

    sealed class TransparentAccountsListEvent {
        abstract val page: Int
        abstract val force: Boolean
        abstract val isFiltered: Boolean
        abstract val hasMorePages: Boolean

        data class Started(override val page: Int, override val force: Boolean, override val isFiltered: Boolean, override val hasMorePages: Boolean = false) : TransparentAccountsListEvent()
        data class Success(override val page: Int, override val force: Boolean, override val isFiltered: Boolean, override val hasMorePages: Boolean) : TransparentAccountsListEvent()
        data class DefaultSuccess(override val page: Int, override val force: Boolean, override val isFiltered: Boolean, override val hasMorePages: Boolean) : TransparentAccountsListEvent()
        data class Error(override val page: Int, override val force: Boolean,  override val isFiltered: Boolean, val error: Throwable, override val hasMorePages: Boolean = false) : TransparentAccountsListEvent()
        object Idle : TransparentAccountsListEvent() {
            override val page: Int
                get() = -1
            override val force: Boolean
                get() = false
            override val isFiltered: Boolean
                get() = false
            override val hasMorePages: Boolean
                get() = false
        }
    }
}