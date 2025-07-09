package com.bluelinelabs.conductor

import com.zj.core.csastest.ui.noAnimTransaction
import com.zj.core.csastest.ui.noAnimTransactionTagged

fun Router.peekBackstackTop(): Controller? {
    return this.backstack.firstOrNull()?.controller
}

fun Router.noAnimSetRoot(controller: Controller) {
    setRoot(noAnimTransaction(controller))
}

fun Router.noAnimSetRootTagged(controller: Controller, controllerTag: String) {
    setRoot(noAnimTransactionTagged(controller, controllerTag))
}