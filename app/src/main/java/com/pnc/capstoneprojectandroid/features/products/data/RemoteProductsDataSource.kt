package com.pnc.capstoneprojectandroid.features.products.data

import retrofit2.http.GET
import retrofit2.http.Path

interface RemoteProductsDataSource : ProductsDataSource {

    @GET("Product")
    override suspend fun getProducts(): List<ProductsDto>

    @GET("Product/{id}")
    override suspend fun getProduct(@Path("id") id: Int): ProductDetailsDto

}