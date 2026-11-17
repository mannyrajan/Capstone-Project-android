package com.pnc.capstoneprojectandroid.core

import com.pnc.capstoneprojectandroid.BuildConfig
import com.pnc.capstoneprojectandroid.features.orders.data.DefaultOrderRepository
import com.pnc.capstoneprojectandroid.features.orders.data.OrderApi
import com.pnc.capstoneprojectandroid.features.orders.domain.OrderRepository
import com.pnc.capstoneprojectandroid.features.products.data.DefaultProductsRepository
import com.pnc.capstoneprojectandroid.features.products.data.DefaultSavedProductsRepository
import com.pnc.capstoneprojectandroid.features.products.domain.ProductsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.pnc.capstoneprojectandroid.features.products.data.ProductsDataSource
import com.pnc.capstoneprojectandroid.features.products.data.RemoteProductsDataSource
import com.pnc.capstoneprojectandroid.features.products.domain.SavedProductsRepository

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideProductsDataSource(retrofit: Retrofit): ProductsDataSource =
        retrofit.create(RemoteProductsDataSource::class.java)

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApi =
        retrofit.create(OrderApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepository(impl: DefaultProductsRepository): ProductsRepository

    @Binds
    abstract fun bindSavedProductsRepository(
        impl: DefaultSavedProductsRepository
    ): SavedProductsRepository
    // Binds the domain-layer interface to its concrete data-layer
    // so Hilt can automatically inject it into ViewModels.
    @Binds
    abstract fun bindOrderRepository(
        impl: DefaultOrderRepository
    ): OrderRepository
}