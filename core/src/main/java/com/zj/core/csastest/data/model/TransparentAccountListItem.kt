package com.zj.core.csastest.data.model

import com.zj.core.csastest.data.DbTransparentAccount

sealed class TransparentAccountListItem {
    object ProgressBarItem : TransparentAccountListItem()
    object ErrorItem : TransparentAccountListItem()

    data class TransparentAccountItem(val account: DbTransparentAccount) : TransparentAccountListItem()

    object EmptyItem : TransparentAccountListItem()

    // todo section
}
