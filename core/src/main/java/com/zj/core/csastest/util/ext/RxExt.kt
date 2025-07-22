package com.zj.core.csastest.util.ext

import com.jakewharton.rxrelay2.BehaviorRelay
import com.zj.core.csastest.util.LOG
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

fun <T, R> Observable<T>.asEvents(
    started: () -> R,
    success: (T) -> R,
    errorr: (Throwable) -> R
): Observable<R> {
    return map { success(it) }
        .doOnError(LOG::e)
        .onErrorReturn { t -> errorr(t) }
        .startWith(started())
}

fun <T> Observable<T>.throwingSubscribe(onNext: Consumer<T>): Disposable {
    return subscribe(onNext, Consumer { t -> throw t })
}

inline fun <T : Any> BehaviorRelay<T>.accept(action: (T) -> T) {
    val value = value
    if (value != null) {
        accept(action(value))
    }
}
