/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.mappers.base

abstract class BaseReverseMapper<From, To> : BaseMapper<From, To>() {

    abstract fun reverse(to: To): From

    fun reverseList(tos: List<To>): List<From> {
        return tos.mapTo(ArrayList(tos.size), this::reverse)
    }

}