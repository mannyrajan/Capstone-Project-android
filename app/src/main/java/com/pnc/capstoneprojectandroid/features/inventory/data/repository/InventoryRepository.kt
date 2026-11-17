
package com.pnc.capstoneprojectandroid.features.inventory.data.repository

import com.pnc.capstoneprojectandroid.features.inventory.data.api.InventoryApi
import com.pnc.capstoneprojectandroid.features.inventory.data.model.InventoryItemDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val api: InventoryApi
) {
    suspend fun getInventory(): List<InventoryItemDto> {
        return api.getInventory()
    }
}
