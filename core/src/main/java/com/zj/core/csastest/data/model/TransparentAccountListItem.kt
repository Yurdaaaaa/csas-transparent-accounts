package com.zj.core.csastest.data.model


sealed class TransparentAccountListItem {
    object ProgressBarItem : TransparentAccountListItem()
    object ErrorItem : TransparentAccountListItem()

    data class TransparentAccountItem(val account: TransparentAccountDomain) : TransparentAccountListItem()

    object EmptyItem : TransparentAccountListItem()

    // todo section
}
