package com.pnc.capstoneprojectandroid.features.products.data

import com.pnc.capstoneprojectandroid.features.products.domain.Products
import com.pnc.capstoneprojectandroid.features.products.domain.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultProductsRepository @Inject constructor(
    private val dataSource: ProductsDataSource
): ProductsRepository {

    override fun observeProducts(): Flow<List<Products>> = flow {
        emit(dataSource.getProducts().map {
            ProductsMapper.mapToDomain(it)
        })
    }

    override suspend fun refreshProducts() = Unit

    override suspend fun getProduct(id: Int): Products =
        ProductsMapper.mapDetailsToDomain(dataSource.getProduct(id))

}