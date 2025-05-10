/**
 * Use this type when the Use Case is should not receive
 * any arguments. This is more readable way.
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.extensions

typealias WithoutParams = Any?

/**
 * Use this function to execute Use Case without parameters.
 */
fun withoutParams(): WithoutParams = null