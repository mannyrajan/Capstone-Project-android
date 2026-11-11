package com.pnc.capstoneprojectandroid.features.products.data

import com.pnc.capstoneprojectandroid.features.products.domain.SavedProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSavedProductsRepository @Inject constructor(
    private val dataStore: SavedProductsDataStore
) : SavedProductsRepository {

    override fun observeSavedIds(): Flow<Set<Int>> = dataStore.savedIds

    override suspend fun toggleSaved(productId: Int) {
        dataStore.toggle(productId)
    }

    override suspend fun isSaved(productId: Int): Boolean =
        productId in dataStore.savedIds.first()
}
