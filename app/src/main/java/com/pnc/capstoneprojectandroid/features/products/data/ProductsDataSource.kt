package com.pnc.capstoneprojectandroid.features.products.data

interface ProductsDataSource {

    suspend fun getProducts(): List<ProductsDto>

    suspend fun getProduct(id: Int): ProductDetailsDto
}