package com.zj.csastest.transparentaccountlist.adapter.holder

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.core.csastest.data.model.TransparentAccountListItem.TransparentAccountItem
import com.zj.core.csastest.util.ext.ifPositionValid
import com.zj.csastest.transparentaccountlist.R
import com.zj.csastest.transparentaccountlist.adapter.TransparentAccountClickListener
import java.math.BigDecimal
import java.text.DecimalFormat

open class TransparentAccountViewHolder(
    val context: Context,
    view: View,
    val czechFormatter: DecimalFormat,
    clickListener: TransparentAccountClickListener,
    itemGetter: (Int) -> TransparentAccountItem
) : RecyclerView.ViewHolder(view) {

    private val accountNameTextView: TextView = view.findViewById(R.id.accountName)
    private val balanceTextView: TextView = view.findViewById(R.id.balance)
    private val currencyTextView: TextView = view.findViewById(R.id.currency)

    init {
        itemView.setOnClickListener {
            ifPositionValid {
                val account = itemGetter(it)
                clickListener.onAccountClicked(account.account.accountNumber)
            }
        }
    }

    fun bind(transparentAccountItem: TransparentAccountItem) {
        accountNameTextView.text = transparentAccountItem.account.name
        setBalance(transparentAccountItem.account.balance)
        setCurrency(transparentAccountItem.account.currency)
    }

    private fun setCurrency(currency: String) {
        val currencySymbol = when (currency.uppercase()) {
            "CZK" -> context.getString(R.string.currency_czk)
            "USD" -> context.getString(R.string.currency_usd)
            "EUR" -> context.getString(R.string.currency_eur)
            else -> currency
        }

        currencyTextView.text = currencySymbol
    }

    fun setBalance(balance: BigDecimal) {
        balanceTextView.text = czechFormatter.format(balance)

        val colorRes = when {
            balance > BigDecimal.ZERO -> R.color.balance_positive
            balance < BigDecimal.ZERO -> R.color.balance_negative
            else -> R.color.balance_neutral
        }

        val color = context.getColor(colorRes)
        balanceTextView.setTextColor(color)
    }

    fun setActualizationDate(actualizationDate: String) {
        // todo
    }
}