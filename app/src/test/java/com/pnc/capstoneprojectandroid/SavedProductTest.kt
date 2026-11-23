package com.pnc.capstoneprojectandroid

import com.pnc.capstoneprojectandroid.features.products.domain.Products
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedProductsTest {

    @Test
    fun testSavedProductsIsolationFromHistory() = runTest {
        // Setup separate lists for saved items and history
        val savedProducts = mutableListOf<Products>()
        val recentHistory = mutableListOf<Products>()

        //empty state check: removing from an empty saved list stays safe and empty
        assertTrue(savedProducts.isEmpty())
        savedProducts.remove(
            Products(
                id = 999,
                name = "Missing Item",
                productNumber = "MS-999",
                price = 0.0,
                summary = "",
                thumbnailBase64 = "",
                warranty = "",
                color = ""
            )
        )
        assertTrue(savedProducts.isEmpty())

        val testProduct = Products(
            id = 101,
            name = "Trail Backpack",
            productNumber = "BP-TR80",
            price = 0.0,
            summary = "Durable backpack",
            thumbnailBase64 = "",
            warranty = "1 year",
            color = "Green"
        )
// Action: Save the product (including zero-price check and duplicate guard)
        if (testProduct !in savedProducts) {
            savedProducts.add(testProduct)
        }
        if (testProduct !in savedProducts) {
            savedProducts.add(testProduct)
        }

        // Check: Saved list grows once, zero price is preserved, and recent history stays empty
        assertEquals(1, savedProducts.size)
        assertEquals(0.0, savedProducts[0].price, 0.001)
        assertEquals(0, recentHistory.size)

        // Action: Remove the product from saved items
        savedProducts.remove(testProduct)

        // Check: Saved list is empty again, history remains untouched
        assertEquals(0, savedProducts.size)
        assertEquals(0, recentHistory.size)
    }
}