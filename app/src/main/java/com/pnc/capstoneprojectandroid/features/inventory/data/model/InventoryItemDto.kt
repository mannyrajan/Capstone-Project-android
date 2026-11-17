
package com.pnc.capstoneprojectandroid.features.inventory.data.model

import androidx.annotation.Keep

@Keep
data class InventoryItemDto(
    val productId: Int,
    val productName: String,
    val productNumber: String,
    val safetyStockLevel: Int,
    val reorderPoint: Int,
    val locationId: Int,
    val locationName: String,
    val shelf: String?,
    val bin: Int?,
    val quantity: Int
)
