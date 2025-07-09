package com.zj.core.csastest.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zj.core.csastest.di.AppComponent
import com.zj.core.csastest.di.AppComponentProvider
import io.reactivex.disposables.CompositeDisposable

internal const val KEY_SAVED_STATE = "saved_state"

abstract class BaseController<VM : ViewModel<SS>, VB : ViewBinding, SS : SavedState> :
    ViewBindingController<VB> {
    constructor() : super()
    constructor(bundle: Bundle) : super(bundle)

    protected val disposables = CompositeDisposable()
    protected lateinit var viewModel: VM

    protected val isViewModelInitialized: Boolean
        get() = ::viewModel.isInitialized

    protected val appComponent: AppComponent
        @Suppress("UNCHECKED_CAST")
        get() = (applicationContext as? AppComponentProvider
            ?: error("Application context does not implement AppComponentProvider"))
            .appComponent

    protected abstract fun onCreateViewModel(appComponent: AppComponent, savedState: SS?): VM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedViewState)

        val activity = activity!!
        if (!::viewModel.isInitialized) {
            val savedState = savedViewState?.getParcelable<SS>(KEY_SAVED_STATE)
            viewModel = onCreateViewModel(appComponent, savedState)
        }
        onViewCreated(activity, viewBinding!!, viewModel, savedViewState)

        return view
    }

    protected abstract fun onViewCreated(
        activity: Activity, viewBinding: VB, viewModel: VM, savedViewState: Bundle?
    )

    open fun hasLightStatusBar() = true
    open fun hasLightNavBar() = true

    override fun onAttach(view: View) {
        super.onAttach(view)
        val activity = activity!!

        (activity as? SystemBarsManager)?.setLightSystemBars(
            hasLightStatusBar(), hasLightNavBar(), this
        )
        onViewAttached(activity, viewBinding!!, viewModel, disposables)
    }

    protected abstract fun onViewAttached(
        activity: Activity, viewBinding: VB, viewModel: VM, disposables: CompositeDisposable
    )

    override fun onDetach(view: View) {
        (activity as? SystemBarsManager)?.clear(this)
        super.onDetach(view)

        disposables.clear()
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        if (::viewModel.isInitialized) {
            val savedState = viewModel.toSavedState()
            if (savedState != null) {
                outState.putParcelable(KEY_SAVED_STATE, savedState)
            }
        }
    }

    override fun onDestroyView(view: View) {
        viewBinding = null
        super.onDestroyView(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::viewModel.isInitialized) {
            viewModel.onCleared()
        }
    }
}