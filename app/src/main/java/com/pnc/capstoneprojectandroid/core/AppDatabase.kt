
package com.pnc.capstoneprojectandroid.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pnc.capstoneprojectandroid.features.products.data.RecentlyViewedDao
import com.pnc.capstoneprojectandroid.features.products.data.RecentlyViewedProductEntity
import com.pnc.capstoneprojectandroid.features.products.data.SavedProductEntity
import com.pnc.capstoneprojectandroid.features.products.data.SavedProductsDao

@Database(
    entities = [
        RecentlyViewedProductEntity::class,
        SavedProductEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentlyViewedDao(): RecentlyViewedDao
    abstract fun savedProductsDao(): SavedProductsDao
}
