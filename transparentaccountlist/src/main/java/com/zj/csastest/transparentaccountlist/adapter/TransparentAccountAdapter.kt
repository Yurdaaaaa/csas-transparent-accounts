package com.zj.csastest.transparentaccountlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zj.core.csastest.data.model.TransparentAccountListItem
import com.zj.core.csastest.data.model.TransparentAccountListItem.*
import com.zj.core.csastest.util.diffutil.Diff
import com.zj.core.csastest.util.diffutil.DiffUtilAdapter
import com.zj.core.csastest.util.diffutil.FLAG_CHANGE__ANONYMOUS
import com.zj.core.csastest.util.diffutil.ListDiffer
import com.zj.core.csastest.util.diffutil.applyDiff
import com.zj.core.csastest.util.ext.applyAll
import com.zj.core.csastest.util.ext.ifHasFlag
import com.zj.csastest.transparentaccountlist.R
import com.zj.csastest.transparentaccountlist.adapter.holder.EmptyViewHolder
import com.zj.csastest.transparentaccountlist.adapter.holder.ProgressViewHolder
import com.zj.csastest.transparentaccountlist.adapter.holder.TransparentAccountViewHolder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val TYPE_TRANSPARENT_ACCOUNT = R.layout.item_transparent_account
private val TYPE_PROGRESS_BAR = R.layout.item_progressbar
private val TYPE_EMPTY_LIST = R.layout.item_empty_list

//private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
//private val thisYearDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm")
//private val pastYearDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")

interface TransparentAccountClickListener {
    fun onAccountClicked(accountNumber: String)
}

interface ITransparentAccountAdapter<T> {
    fun setData(
        list: List<TransparentAccountListItem>,
        preCalculate: () -> Int,
        onCalculated: (Diff<TransparentAccountListItem>, Int) -> Unit)
    fun isNotEmpty(): Boolean
    fun isEmpty(): Boolean
    fun clearDiffer()
    fun clearData()
}

class TransparentAccountAdapter(
    private val context: Context,
    private val clickListener: TransparentAccountClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ITransparentAccountAdapter<TransparentAccountListItem> {

    private val listDiffer = TransparentAccountListDiffer()
    private val layoutInflater = LayoutInflater.from(context)
    private var items = emptyList<Any>()
    private val itemGetter: (Int) -> Any = { items[it] }

    private val czechLocale: Locale by lazy {
        Locale.Builder()
            .setLanguage("cs")
            .setRegion("CZ")
            .build()
    }

    private val czechFormatter: DecimalFormat by lazy {
        DecimalFormat(
            "#,##0.################",
            DecimalFormatSymbols(czechLocale)
        ).apply {
            isGroupingUsed = true
        }
    }

    override fun setData(list: List<TransparentAccountListItem>, preCalculate: () -> Int, onCalculated: (Diff<TransparentAccountListItem>, Int) -> Unit) {
        println("TransparentAccountAdapter setData size: ${list.size} list: $list")

        listDiffer.calculate(list) { diff ->
            val cached = preCalculate()
            items = diff.list
            applyDiff(diff.result)
            onCalculated(diff, cached)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TRANSPARENT_ACCOUNT -> {
                val view = layoutInflater.inflate(TYPE_TRANSPARENT_ACCOUNT, parent, false)
                TransparentAccountViewHolder(context, view, czechFormatter, clickListener, itemGetter as (Int) -> TransparentAccountItem)
            }
            TYPE_PROGRESS_BAR -> {
                val view = layoutInflater.inflate(TYPE_PROGRESS_BAR, parent, false)
                ProgressViewHolder(view)
            }
            TYPE_EMPTY_LIST -> {
                val view = layoutInflater.inflate(TYPE_EMPTY_LIST, parent, false)
                EmptyViewHolder(view)
            }
            else -> error("Unknown view type '$viewType'")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        val handled = payloads.applyAll {
            ifHasFlag(it, FLAG_CHANGE__ACTUALIZATION_DATE) {
                (holder as? TransparentAccountViewHolder)?.setActualizationDate((items[position] as TransparentAccountItem).account.actualizationDate)
            }
            ifHasFlag(it, FLAG_CHANGE__BALANCE) {
                (holder as? TransparentAccountViewHolder)?.setBalance((items[position] as TransparentAccountItem).account.balance)
            }
        }

        if (!handled) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val viewType = getItemViewType(position)) {
            TYPE_TRANSPARENT_ACCOUNT -> {
                (holder as TransparentAccountViewHolder).bind(items[position] as TransparentAccountItem)
            }
            TYPE_PROGRESS_BAR -> Unit
            TYPE_EMPTY_LIST -> (holder as EmptyViewHolder).bind()
            else -> error("Unknown view type '$viewType'")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is TransparentAccountItem -> TYPE_TRANSPARENT_ACCOUNT
            is ProgressBarItem -> TYPE_PROGRESS_BAR
            is EmptyItem -> TYPE_EMPTY_LIST
            else -> error("Unknown type '$item'")
        }
    }

    override fun getItemCount() = items.size

    override fun isNotEmpty(): Boolean {
        return items.isNotEmpty()
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun clearDiffer() {
        listDiffer.clear()
    }

    override fun clearData() {
        items = listOf()
        notifyDataSetChanged()
    }
}

internal const val FLAG_CHANGE__ACTUALIZATION_DATE = 0b10
internal const val FLAG_CHANGE__BALANCE = 0b100

internal class TransparentAccountListDiffer : ListDiffer<TransparentAccountListItem>(
    areIdenticalPredicate = { old, new ->
        when {
            old is TransparentAccountItem && new is TransparentAccountItem -> old.account.accountNumber == new.account.accountNumber
            old is ProgressBarItem && new is ProgressBarItem -> true
            old is ErrorItem && new is ErrorItem -> true
            else -> false
        }
    },
    areEqualPredicate = { old, new ->
        when {
            old is TransparentAccountItem && new is TransparentAccountItem -> messageItemEquals(old, new)
            old is ProgressBarItem && new is ProgressBarItem -> true
            old is ErrorItem && new is ErrorItem -> true
            else -> old == new
        }
    },
    payloadSelector = { old, new ->
        when {
            old is TransparentAccountItem && new is TransparentAccountItem -> TransparentAccountItemDiffUtilAdapter.getChangePayload(old, new)
            else -> null
        }
    },
    detectMoves = true
)

private fun messageItemEquals(old: TransparentAccountItem, new: TransparentAccountItem): Boolean {
    return TransparentAccountItemDiffUtilAdapter.areEqual(old, new)
}

object TransparentAccountItemDiffUtilAdapter : DiffUtilAdapter<TransparentAccountItem>(supportsPayloads = true) {
    override fun fullRebindChanges(l: TransparentAccountItem, r: TransparentAccountItem): Int {
        var flags = super.fullRebindChanges(l, r)

        if (l.account.accountNumber != r.account.accountNumber) flags = flags or FLAG_CHANGE__ANONYMOUS
        if (l.account.currency != r.account.currency) flags = flags or FLAG_CHANGE__ANONYMOUS
        if (l.account.name != r.account.name) flags = flags or FLAG_CHANGE__ANONYMOUS
        if (l.account.iban != r.account.iban) flags = flags or FLAG_CHANGE__ANONYMOUS
        if (l.account.bankCode != r.account.bankCode) flags = flags or FLAG_CHANGE__ANONYMOUS

        return flags
    }

    override fun payloadRebindChanges(l: TransparentAccountItem, r: TransparentAccountItem): Int {
        var flags = super.payloadRebindChanges(l, r)

        if (l.account.actualizationDate != r.account.actualizationDate) flags = flags or FLAG_CHANGE__ACTUALIZATION_DATE
        if (l.account.balance != r.account.balance) flags = flags or FLAG_CHANGE__BALANCE

        return flags
    }
}
