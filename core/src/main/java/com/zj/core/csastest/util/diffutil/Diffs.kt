package com.zj.core.csastest.util.diffutil

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class DiffCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val areIdenticalPredicate: (T, T) -> Boolean,
    private val areEqualPredicate: (T, T) -> Boolean,
    private val payloadSelector: ((T, T) -> Any?)?
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        areIdenticalPredicate(oldList[oldItemPosition], newList[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        areEqualPredicate(oldList[oldItemPosition], newList[newItemPosition])

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return payloadSelector?.invoke(oldList[oldItemPosition], newList[newItemPosition])
    }
}

data class Diff<T>(val list: List<T>, val result: DiffUtil.DiffResult?)

fun <T> Observable<out List<T>>.uiDiff(areIdenticalPredicate: (T, T) -> Boolean) =
    uiDiff(areIdenticalPredicate = areIdenticalPredicate, payloadSelector = null)

fun <T> Observable<out List<T>>.uiDiff(
    areIdenticalPredicate: (T, T) -> Boolean,
    areEqualPredicate: (T, T) -> Boolean = { old, new -> old == new },
    payloadSelector: ((T, T) -> Any?)? = null,
    detectMoves: Boolean = false
): Observable<Diff<T>> {
    val counter = AtomicInteger(0)
    val last = AtomicReference<List<T>>(null)
    return distinctUntilChanged { old, new -> old === new }
        .switchMap {
            val index = counter.getAndIncrement()
            if (index == 0) {
                Observable.just(Diff(it, null))
            } else {
                Observable.fromCallable {
                    val oldList = last.get()!!
                    val newList = it
                    val diffCallback =
                        DiffCallback(
                            oldList,
                            newList,
                            areIdenticalPredicate,
                            areEqualPredicate,
                            payloadSelector
                        )
                    val diff = DiffUtil.calculateDiff(diffCallback, detectMoves)
                    Diff(newList, diff)
                }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }
        .doOnNext { diff -> last.set(diff.list) }
}

fun RecyclerView.Adapter<*>.applyDiff(result: DiffUtil.DiffResult?): Boolean {
    return if (result == null) {
        notifyDataSetChanged()
        false
    } else {
        result.dispatchUpdatesTo(this)
        true
    }
}