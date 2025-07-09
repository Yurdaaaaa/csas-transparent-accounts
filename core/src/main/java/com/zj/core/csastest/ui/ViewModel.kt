package com.zj.core.csastest.ui

import android.os.Parcelable
import io.reactivex.disposables.CompositeDisposable

abstract class ViewModel<SS : SavedState> {
    protected val disposables = CompositeDisposable()

    open fun toSavedState(): SS? = null

    open fun onCleared() {
        disposables.clear()
    }
}

interface SavedState : Parcelable
