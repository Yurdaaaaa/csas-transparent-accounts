package com.zj.core.csastest.data.mapper

import com.zj.core.csastest.data.DbTransparentAccount
import com.zj.core.csastest.data.model.TransparentAccountListItem
import com.zj.core.csastest.data.model.TransparentAccountListItem.*

const val NUMBER_OF_PROGRESS_BAR_ITEMS = 1

object TransparentAccountItemMapper {

    fun mapAccounts(
        accounts: List<DbTransparentAccount>,
        addProgressBar: Boolean,
        addError: Boolean,
        shouldCreateDateSections: Boolean = false
    ): MutableList<TransparentAccountListItem> {

        // todo date sections

        val listItems = mutableListOf<TransparentAccountListItem>()

        processAccounts(accounts, listItems)

        if (listItems.isEmpty() && !addProgressBar) {
            listItems.add(EmptyItem)
        }

        if (addProgressBar) {
            repeat(NUMBER_OF_PROGRESS_BAR_ITEMS) { listItems.add(ProgressBarItem) }
        }
        if (addError) {
            listItems.add(ErrorItem)
        }

        return listItems
    }

    private fun processAccounts(accounts: List<DbTransparentAccount>, listItems: MutableList<TransparentAccountListItem>) {
        accounts.forEach { account ->
            listItems.add(TransparentAccountItem(account))
        }
    }
}