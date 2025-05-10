/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.repository.local.base

import kotlinx.coroutines.flow.Flow

interface BaseLocalRepositoryWithDataMethods<T> : BaseLocalRepository<T> {

    fun getAll(): T?

    fun addAll(data: T)

    fun replaceAll(data: T)

    fun subscribeToAll(): Flow<T?>

}