package com.pnc.capstoneprojectandroid.features.products.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_products")
data class SavedProductEntity(
    @PrimaryKey val productId: Int,
    val savedAt: Long
)