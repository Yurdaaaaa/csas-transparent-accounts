package com.zj.core.csastest.util

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class ListAccountsItemScrollListener : RecyclerView.OnScrollListener() {

    fun attachToRecyclerView(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        recyclerView.addOnScrollListener(this)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (isLastItemVisible(recyclerView)) {
                onScrolledToEnd()
            }
        }
    }

    private fun isLastItemVisible(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            // Check if the last visible item position is the last item in the list
            return lastVisibleItemPosition >= totalItemCount - 1
        }
        return false
    }

    abstract fun onScrolledToEnd()
}

