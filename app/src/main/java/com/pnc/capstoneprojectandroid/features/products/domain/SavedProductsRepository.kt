package com.pnc.capstoneprojectandroid.features.products.domain

import kotlinx.coroutines.flow.Flow

interface SavedProductsRepository {
    fun observeSaved(): Flow<List<Products>>
    fun observeSavedIds(): Flow<Set<Int>>
    suspend fun toggleSaved(product: Products)
    suspend fun isSaved(productId: Int): Boolean
}