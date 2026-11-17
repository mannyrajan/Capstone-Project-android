
package com.pnc.capstoneprojectandroid.features.products.domain

import kotlinx.coroutines.flow.Flow

interface RecentlyViewedRepository {

    fun observeRecentlyViewed(): Flow<List<Products>>

    suspend fun addRecentlyViewed(product: Products)

    suspend fun clearRecentlyViewed()
}
