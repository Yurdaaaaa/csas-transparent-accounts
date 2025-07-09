package com.zj.core.csastest.util.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.zj.core.csastest.util.ext.throwingSubscribe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

open class ListDiffer<T>(
    private val areIdenticalPredicate: (T, T) -> Boolean,
    private val areEqualPredicate: (T, T) -> Boolean = { old, new -> old == new },
    private val payloadSelector: ((T, T) -> Any?)? = null,
    private val detectMoves: Boolean = false,
    private val mainThreadScheduler: Scheduler = AndroidSchedulers.mainThread(),
    private val computationScheduler: Scheduler = Schedulers.computation()
) {
    private var previousList: List<T>? = null
    private var disposable: Disposable? = null

    fun calculate(list: List<T>, onCalculated: (Diff<T>) -> Unit) {
        val previousList = this.previousList
        if (previousList === list) return

        if (previousList == null) {
            onCalculated(Diff(list, null))
            this.previousList = list
        } else {
            disposable?.dispose()
            disposable = calculateDiff(previousList, list, onCalculated)
        }
    }

    fun clear() {
        val disposable = disposable
        if (disposable != null) {
            disposable.dispose()
            this.disposable = null
        }
        previousList = null
    }

    private fun calculateDiff(previousList: List<T>, list: List<T>, onCalculated: (Diff<T>) -> Unit): Disposable {
        return Observable
            .fromCallable {
                val diffCallback = DiffCallback(
                    previousList, list, areIdenticalPredicate, areEqualPredicate, payloadSelector
                )
                val diff = DiffUtil.calculateDiff(diffCallback, detectMoves)
                Diff(list, diff)
            }
            .subscribeOn(computationScheduler)
            .observeOn(mainThreadScheduler)
            .throwingSubscribe {
                onCalculated(it)
                this.previousList = it.list
            }
    }
}