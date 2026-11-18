package com.pnc.capstoneprojectandroid.features.products.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedProductDao {

    @Query("SELECT productId FROM saved_products ORDER BY savedAt DESC")
    fun observeSavedProductIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProduct(item: SavedProductEntity)

    @Query("DELETE FROM saved_products WHERE productId = :productId")
    suspend fun removeSavedProduct(productId: Int)
}