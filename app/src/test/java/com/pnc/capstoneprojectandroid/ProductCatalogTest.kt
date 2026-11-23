package com.pnc.capstoneprojectandroid

import com.pnc.capstoneprojectandroid.features.products.domain.Products
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductsCatalogTest {
    private val sampleProducts = listOf(
        Products(
            id = 1,
            name = "Mountain Bike",
            productNumber = "BK-M68S-42",
            price = 499.99,
            summary = "A great bike",
            thumbnailBase64 = "",
            warranty = "1 year",
            color = "Red"
        ),
        Products(
            id = 2,
            name = "Road Helmet",
            productNumber = "HL-R509",
            price = 34.99,
            summary = "Safe helmet",
            thumbnailBase64 = "",
            warranty = "6 months",
            color = "Black"
        ),
        Products(
            id = 3,
            name = "Touring Helmet",
            productNumber = "HL-R510",
            price = 60.0,
            summary = "Touring helmet",
            thumbnailBase64 = "",
            warranty = "1 year",
            color = "Black"
        ),
        Products(
            id = 4,
            name = "Free Sticker",
            productNumber = "ST-000",
            price = 0.0,
            summary = "Promo item",
            thumbnailBase64 = "",
            warranty = "None",
            color = "Black"
        )
    )

    @Test
    fun testCaseInsensitiveSearch() = runTest {
        // Define search query
        val query = "mountain"
        // Execute search filter logic matching name or product number case-insensitively
        val results = sampleProducts.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.productNumber.contains(query, ignoreCase = true)
        }

        // Assert that exactly 1 result was found and it is the Mountain Bike
        assertEquals(1, results.size)
        assertEquals("Mountain Bike", results[0].name)
    }

    @Test
    fun testCombinedFilteringAndSorting() = runTest {
        // Filter by color (Black) AND price (< 60.0), then sort by price ascending
        val filteredAndSorted = sampleProducts
            .filter { it.color == "Black" && it.price < 60.0 }
            .sortedBy { it.price }

        assertEquals(2, filteredAndSorted.size)
        assertEquals("Free Sticker", filteredAndSorted[0].name)
        assertEquals("Road Helmet", filteredAndSorted[1].name)
    }

    @Test
    fun testEmptySearchAndFilterResults() = runTest {
        val noMatchQueryResults = sampleProducts.filter {
            it.name.contains("nonexistent-item", ignoreCase = true) ||
                    it.productNumber.contains("nonexistent-item", ignoreCase = true)
        }
        val noMatchFilterResults = sampleProducts.filter {
            it.color == "Yellow" && it.price < 10.0
        }

        assertTrue(noMatchQueryResults.isEmpty())
        assertTrue(noMatchFilterResults.isEmpty())
    }

    @Test
    fun testZeroPriceHandling() = runTest {
        val zeroPriceProducts = sampleProducts.filter { it.price == 0.0 }

        assertEquals(1, zeroPriceProducts.size)
        assertEquals(0.0, zeroPriceProducts[0].price, 0.001)
    }
}