
package com.pnc.capstoneprojectandroid.features.products.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyViewedDao {

    @Query("""
        SELECT * FROM recently_viewed_products
        ORDER BY viewedAt DESC
        LIMIT 10
    """)
    fun observeRecentlyViewed(): Flow<List<RecentlyViewedProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: RecentlyViewedProductEntity)

    @Query("""
        DELETE FROM recently_viewed_products
        WHERE id NOT IN (
            SELECT id FROM recently_viewed_products
            ORDER BY viewedAt DESC
            LIMIT 10
        )
    """)
    suspend fun keepLatestTen()

    @Query("DELETE FROM recently_viewed_products")
    suspend fun clearHistory()
}
