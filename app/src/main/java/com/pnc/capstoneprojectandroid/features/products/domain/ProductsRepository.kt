package com.pnc.capstoneprojectandroid.features.products.domain

import kotlinx.coroutines.flow.Flow

interface ProductsRepository {

    fun observeProducts(): Flow<List<Products>>
    suspend fun refreshProducts(): List<Products>

    suspend fun getProduct(id: Int): Products

}