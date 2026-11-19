package com.pnc.capstoneprojectandroid

import com.pnc.capstoneprojectandroid.features.products.domain.Products
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductsCatalogTest {

    @Test
    fun testCaseInsensitiveSearch() = runTest {
        // Setup mock products with different names, numbers, and attributes
        val sampleProducts = listOf(
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
            )
        )

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
        val sampleProducts = listOf(
            Products(id = 1, name = "Basic Helmet", productNumber = "HL-01", price = 30.0, color = "Black", summary = "", thumbnailBase64 = "", warranty = ""),
            Products(id = 2, name = "Pro Helmet", productNumber = "HL-02", price = 80.0, color = "Black", summary = "", thumbnailBase64 = "", warranty = ""),
            Products(id = 3, name = "Touring Helmet", productNumber = "HL-03", price = 50.0, color = "Blue", summary = "", thumbnailBase64 = "", warranty = "")
        )

        // Filter by color (Black) AND price (< 60.0), then sort by price ascending
        val filteredAndSorted = sampleProducts
            .filter { it.color == "Black" && it.price < 60.0 }
            .sortedBy { it.price }

        assertEquals(1, filteredAndSorted.size)
        assertEquals("Basic Helmet", filteredAndSorted[0].name)
    }
}