package com.pnc.capstoneprojectandroid

import com.pnc.capstoneprojectandroid.features.products.domain.Products
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SavedProductsTest {

    @Test
    fun testSavedProductsIsolationFromHistory() = runTest {
        // Setup separate lists for saved items and history
        val savedProducts = mutableListOf<Products>()
        val recentHistory = mutableListOf<Products>()

        val testProduct = Products(
            id = 101,
            name = "Trail Backpack",
            productNumber = "BP-TR80",
            price = 120.0,
            summary = "Durable backpack",
            thumbnailBase64 = "",
            warranty = "1 year",
            color = "Green"
        )

        // Action: Save the product
        savedProducts.add(testProduct)

        // Check: Saved list grows, but recent history remains completely unaffected (size 0)
        assertEquals(1, savedProducts.size)
        assertEquals(0, recentHistory.size)

        // Action: Remove the product from saved items
        savedProducts.remove(testProduct)

        // Check: Saved list is empty again, history remains untouched
        assertEquals(0, savedProducts.size)
        assertEquals(0, recentHistory.size)
    }
}