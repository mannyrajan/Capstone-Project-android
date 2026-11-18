

package com.pnc.capstoneprojectandroid.features.products.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedProductsDao {
    @Query("SELECT * FROM saved_products ORDER BY savedAt DESC")
    fun observeSaved(): Flow<List<SavedProductEntity>>

    @Query("SELECT id FROM saved_products")
    fun observeSavedIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: SavedProductEntity)

    @Query("DELETE FROM saved_products WHERE id = :productId")
    suspend fun deleteById(productId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_products WHERE id = :productId)")
    suspend fun isSaved(productId: Int): Boolean
}