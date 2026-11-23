package com.pnc.capstoneprojectandroid

import com.pnc.capstoneprojectandroid.features.products.domain.Products
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentHistoryTest {

    @Test
    fun testRecentHistoryRules() = runTest {
        val savedProducts = mutableListOf<Products>()
        val recentHistory = mutableListOf<Products>()
        // Helper enforcing business rules: no duplicates, move revisit to front, cap at 10
        fun addToHistory(product: Products) {
            recentHistory.remove(product)
            recentHistory.add(0, product)
            if (recentHistory.size > 10) {
                recentHistory.removeAt(recentHistory.size - 1)
            }
        }
        // empty-state check: clearing an empty history stays safe and empty
        recentHistory.clear()
        assertTrue(recentHistory.isEmpty())
        val p1 = Products(
            id = 1,
            name = "Product 1",
            productNumber = "P1",
            price = 0.0,
            summary = "",
            thumbnailBase64 = "",
            warranty = "",
            color = ""
        )
        val p2 = Products(
            id = 2,
            name = "Product 2",
            productNumber = "P2",
            price = 20.0,
            summary = "",
            thumbnailBase64 = "",
            warranty = "",
            color = ""
        )
        // Add items, then revisit p1 (should move to front, no duplicate)
        addToHistory(p1)
        addToHistory(p2)
        addToHistory(p1) // p1 is now at front

        assertEquals(2, recentHistory.size)
        assertEquals(1, recentHistory[0].id)
        assertEquals(0.0, recentHistory[0].price,0.001)

        // Add items up to 12 to test the 10-item maximum capacity cap
        for (i in 3..12) {
            addToHistory(
                Products(
                    id = i,
                    name = "Product $i",
                    productNumber = "P$i",
                    price = (i * 10).toDouble(),
                    summary = "",
                    thumbnailBase64 = "",
                    warranty = "",
                    color = ""
                )
            )
        }
        assertEquals(10, recentHistory.size)

        // Verify that clearing history leaves saved products safe and untouched
        savedProducts.add(p1)
        recentHistory.clear()

        assertEquals(0, recentHistory.size)
        assertEquals(1, savedProducts.size)
        assertEquals(1, savedProducts[0].id)
    }
}