package com.pnc.capstoneprojectandroid.features.products.domain

import kotlinx.coroutines.flow.Flow

interface SavedProductsRepository {
    fun observeSavedIds(): Flow<Set<Int>>
    suspend fun toggleSaved(productId: Int)
    suspend fun isSaved(productId: Int): Boolean
}
