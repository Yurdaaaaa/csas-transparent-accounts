package com.zj.core.csastest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.zj.core.csastest.view.TintToolbar
import com.zj.csastest.core.R

fun noAnimTransaction(controller: Controller) = RouterTransaction.with(controller)
fun noAnimTransactionTagged(controller: Controller, controllerTag: String) = RouterTransaction.with(controller)
    .tag(controllerTag)

abstract class ViewBindingController<VB : ViewBinding> : Controller {
    constructor() : super()
    constructor(bundle: Bundle) : super(bundle)

    protected var viewBinding: VB? = null

    protected abstract fun layoutRes(): Int

    protected open fun hasUpButton() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = onInflateView(layoutRes(), inflater, container)
        onViewCreated(view, savedViewState)
        return view
    }

    protected open fun onInflateView(layoutRes: Int, inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(layoutRes, container, false)
    }

    protected open fun onViewCreated(view: View, savedViewState: Bundle?) {
        viewBinding = onCreateViewBinding(view).apply {
            toolbar?.apply {
                onSetupToolbar(this)
            }
        }
        onViewBindingCreated(view, viewBinding!!, savedViewState)
    }

    protected open fun onViewBindingCreated(view: View, viewBinding: VB, savedViewState: Bundle?) {}

    protected abstract fun onCreateViewBinding(view: View): VB

    protected open fun onSetupToolbar(toolbar: TintToolbar) {
        if (hasUpButton()) {
            toolbar.setNavigationIcon(R.drawable.ic_up)
            toolbar.setNavigationOnClickListener(::handleNavButtonClicked)
        }
        toolbar.setOnMenuItemClickListener(::handleMenuItemClicked)
        toolbar.menuInvalidatedListener = ::handleMenuInvalidated
    }

    protected open fun handleNavButtonClicked(view: View) {
        router.popController(this)
    }

    protected open fun handleMenuInvalidated(menu: Menu) {}

    protected open fun handleMenuItemClicked(item: MenuItem) = false

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        viewBinding = null
    }
}