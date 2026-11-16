
package com.pnc.capstoneprojectandroid.features.products.data

import com.pnc.capstoneprojectandroid.features.products.domain.Products
import com.pnc.capstoneprojectandroid.features.products.domain.RecentlyViewedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRecentlyViewedRepository @Inject constructor(
    private val recentlyViewedDao: RecentlyViewedDao
) : RecentlyViewedRepository {

    override fun observeRecentlyViewed(): Flow<List<Products>> {
        return recentlyViewedDao.observeRecentlyViewed()
            .map { entities -> entities.map { it.toProduct() } }
    }

    override suspend fun addRecentlyViewed(product: Products) {
        recentlyViewedDao.insert(
            RecentlyViewedProductEntity.fromProduct(product)
        )
        recentlyViewedDao.keepLatestTen()
    }

    override suspend fun clearRecentlyViewed() {
        recentlyViewedDao.clearHistory()
    }
}
