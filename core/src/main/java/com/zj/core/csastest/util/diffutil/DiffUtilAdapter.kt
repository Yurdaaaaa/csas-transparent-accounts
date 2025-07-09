package com.zj.core.csastest.util.diffutil

const val FLAG_NO_CHANGE = 0
const val FLAG_CHANGE__ANONYMOUS = 1

open class DiffUtilAdapter<T : Any>(private val supportsPayloads: Boolean = false) {

    fun areEqual(l: T, r: Any): Boolean {
        if (l::class != r::class) return false
        @Suppress("UNCHECKED_CAST") val _r = r as T

        val fullFlags = fullRebindChanges(l, _r)
        if (fullFlags.hasChanges()) return false

        val payloadFlags = payloadRebindChanges(l, _r)
        if (payloadFlags.hasChanges()) return false

        return true
    }

    fun getChangePayload(l: T, r: Any): Any? {
        if (!supportsPayloads) return null
        if (l::class != r::class) return null
        @Suppress("UNCHECKED_CAST") val _r = r as T

        val fullFlags = fullRebindChanges(l, _r)
        if (fullFlags.hasChanges()) return null

        val payloadFlags = payloadRebindChanges(l, _r)
        require(payloadFlags.hasChanges())
        return payloadFlags
    }

    protected open fun fullRebindChanges(l: T, r: T): Int {
        return FLAG_NO_CHANGE
    }

    protected open fun payloadRebindChanges(l: T, r: T): Int {
        return FLAG_NO_CHANGE
    }
}

private fun Int.hasChanges(): Boolean {
    return this != 0
}