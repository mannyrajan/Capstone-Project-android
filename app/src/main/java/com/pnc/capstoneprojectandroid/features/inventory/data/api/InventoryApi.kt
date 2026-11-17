package com.pnc.capstoneprojectandroid.features.inventory.data.api

import com.pnc.capstoneprojectandroid.features.inventory.data.model.InventoryItemDto
import retrofit2.http.GET

interface InventoryApi {

    @GET("api/inventory")
    suspend fun getInventory(): List<InventoryItemDto>
}
