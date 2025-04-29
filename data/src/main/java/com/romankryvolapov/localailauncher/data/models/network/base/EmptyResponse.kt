/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.models.network.base

class EmptyResponse

@Suppress("UNCHECKED_CAST")
fun <T> getEmptyResponse(): T {
    return EmptyResponse() as T
}