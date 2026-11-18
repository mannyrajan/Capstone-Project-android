package com.pnc.capstoneprojectandroid.features.products.data

import com.pnc.capstoneprojectandroid.features.products.domain.Products
import com.pnc.capstoneprojectandroid.features.products.domain.SavedProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSavedProductsRepository @Inject constructor(
    private val dao: SavedProductsDao
) : SavedProductsRepository {

    override fun observeSaved(): Flow<List<Products>> =
        dao.observeSaved().map { entities -> entities.map { it.toProduct() } }

    override fun observeSavedIds(): Flow<Set<Int>> =
        dao.observeSavedIds().map { it.toSet() }

    override suspend fun toggleSaved(product: Products) {
        if (dao.isSaved(product.id)) {
            dao.deleteById(product.id)
        } else {
            dao.insert(SavedProductEntity.fromProduct(product))
        }
    }

    override suspend fun isSaved(productId: Int): Boolean =
        dao.isSaved(productId)
}