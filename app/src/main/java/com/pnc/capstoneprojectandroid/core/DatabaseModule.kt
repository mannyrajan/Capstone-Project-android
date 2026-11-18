
package com.pnc.capstoneprojectandroid.core

import android.content.Context
import androidx.room.Room
import com.pnc.capstoneprojectandroid.features.products.data.RecentlyViewedDao
import com.pnc.capstoneprojectandroid.features.products.data.DefaultRecentlyViewedRepository
import com.pnc.capstoneprojectandroid.features.products.data.SavedProductDao
import com.pnc.capstoneprojectandroid.features.products.domain.RecentlyViewedRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "adventureworks_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()
        // Safe fallback to avoid schema migration crashes when updating database entities
    }

    @Provides
    fun provideRecentlyViewedDao(
        database: AppDatabase
    ): RecentlyViewedDao {
        return database.recentlyViewedDao()
    }

    @Provides
    fun provideSavedProductDao(
        database: AppDatabase
    ): SavedProductDao {
        return database.savedProductDao()
    }
}

// moved this binding to the NetworkModule