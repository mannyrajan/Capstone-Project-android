
package com.pnc.capstoneprojectandroid.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pnc.capstoneprojectandroid.features.products.data.RecentlyViewedDao
import com.pnc.capstoneprojectandroid.features.products.data.RecentlyViewedProductEntity

@Database(
    entities = [RecentlyViewedProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentlyViewedDao(): RecentlyViewedDao
}
