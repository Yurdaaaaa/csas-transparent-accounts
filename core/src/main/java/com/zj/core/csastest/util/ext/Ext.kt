package com.zj.core.csastest.util.ext

fun <A, B> pairOf(a: A, b: B) = Pair(a, b)
fun <A, B> tupleOf(a: A, b: B) = Pair(a, b)
fun <A, B, C> tupleOf(a: A, b: B, c: C) = Triple(a, b, c)

fun Int.hasFlag(flag: Int): Boolean {
    return this and flag == flag
}