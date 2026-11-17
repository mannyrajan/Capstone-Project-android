package com.pnc.capstoneprojectandroid.features.orders.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderApi {
    @GET("Order/customer")
    suspend fun getCustomerOrders(): List<CustomerOrderLineDto>

    @GET("Order/customer/{id}")
    suspend fun getCustomerOrder(@Path("id") id: Int): CustomerOrderDetailDto
}

// removed and replaced with retrofit instance on network module

//object OrderApiFactory {
//    private const val BASE_URL = "https://api.bootcampcentral.com/"
//
//    fun create(): OrderApi {
//        val logging = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BASIC
//        }
//        val client = OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(OrderApi::class.java)
//    }
//}

