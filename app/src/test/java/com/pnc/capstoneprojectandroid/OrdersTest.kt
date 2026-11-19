package com.pnc.capstoneprojectandroid

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

data class TestOrder(
    val id: Int,
    val date: Date,
    val status: String
)

class OrdersTest {

    @Test
    fun testOrderDateOrdering() = runTest {
        val olderDate = Date(1000000L)
        val newerDate = Date(2000000L)

        val orders = listOf(
            TestOrder(id = 1, date = olderDate, status = "Shipped"),
            TestOrder(id = 2, date = newerDate, status = "Processing")
        )

        // Sort descending so newest date appears first
        val sortedOrders = orders.sortedByDescending { it.date }

        assertEquals(2, sortedOrders[0].id) // Newest date should be first
        assertEquals(1, sortedOrders[1].id) // Oldest date should be second
    }

    @Test
    fun testUnknownOrderStatusBehavior() = runTest {
        // Safe parser handling standard statuses and fallback for unknown strings
        fun mapOrderStatus(status: String): String {
            return when (status.lowercase()) {
                "shipped", "processing", "delivered", "cancelled" -> status
                else -> "Unknown"
            }
        }

        val validOrder = TestOrder(id = 1, date = Date(), status = "Shipped")
        val unknownOrder = TestOrder(id = 2, date = Date(), status = "AlienAbducted")

        assertEquals("Shipped", mapOrderStatus(validOrder.status))
        assertEquals("Unknown", mapOrderStatus(unknownOrder.status)) // fallback
    }
}