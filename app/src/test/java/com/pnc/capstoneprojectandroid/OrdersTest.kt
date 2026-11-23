package com.pnc.capstoneprojectandroid

import com.pnc.capstoneprojectandroid.features.orders.domain.CustomerOrder
import com.pnc.capstoneprojectandroid.features.orders.domain.OrderLine
import com.pnc.capstoneprojectandroid.features.orders.domain.OrderStatus
import com.pnc.capstoneprojectandroid.features.orders.domain.OrderStatusClassifier
import com.pnc.capstoneprojectandroid.features.orders.domain.formatOrderDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.Date
import kotlin.collections.firstOrNull


class OrdersTest {
    private fun sampleOrder(
        orderNumber: Int = 1001,
        orderDate: String? = "2026-10-01T10:30:00",
        shipDate: String? = "2026-10-02T14:00:00",
        status: Int = 5,
        statusName: String? = "Shipped",
        lines: List<OrderLine> = listOf(
            OrderLine(
                id = 1,
                productName = "Mountain Bike",
                quantity = 1,
                unitPrice = 499.99,
                lineTotal = 499.99
            )
        )
    ): CustomerOrder = CustomerOrder(
        orderNumber = orderNumber,
        customerId = 42,
        customerName = "Alex Morgan",
        orderDate = orderDate,
        shipDate = shipDate,
        status = status,
        statusName = statusName,
        representativeLineId = lines.firstOrNull()?.id ?: 0,
        lines = lines
    )

    @Test
    fun testOrderDateOrdering() = runTest {
        val olderOrder = sampleOrder(
            orderNumber = 1,
            orderDate = "2026-09-01T08:00:00"
        )
        val newerOrder = sampleOrder(
            orderNumber = 2,
            orderDate = "2026-10-15T08:00:00"
        )
        // Sort descending so newest date appears first
        val sortedOrders = listOf(olderOrder, newerOrder)
            .sortedByDescending { it.orderDate ?: "" }

        assertEquals(2, sortedOrders[0].orderNumber) // Newest date should be first
        assertEquals(1, sortedOrders[1].orderNumber) // Oldest date should be second
        assertEquals("Oct 15, 2026", formatOrderDate(sortedOrders[0].orderDate))
    }

    @Test
    fun testKnownAndUnknownOrderStatusBehavior() = runTest {
        val shippedOrder = sampleOrder(status = 5, statusName = "Shipped")
        assertEquals(OrderStatus.SHIPPED, OrderStatusClassifier.classify(5, "Shipped"))
        assertEquals("Shipped", shippedOrder.customerFacingStatus)
        assertEquals("Your order is on the way.", shippedOrder.statusExplanation)

        val unknownOrder = sampleOrder(status = 999, statusName = "AlienAbducted")
        assertEquals(OrderStatus.UNKNOWN, OrderStatusClassifier.classify(999, "AlienAbducted"))
        assertEquals("Unknown Status", unknownOrder.customerFacingStatus)
        assertEquals(
            "Status details are currently unavailable.",
            unknownOrder.statusExplanation
        )
    }

    @Test
    fun testEmptyLinesAndZeroValues() = runTest {
        val zeroLine = OrderLine(
            id = 10,
            productName = "Free Sample",
            quantity = 0,
            unitPrice = 0.0,
            lineTotal = 0.0
        )
        val zeroValueOrder = sampleOrder(
            orderNumber = 50,
            orderDate = null,
            shipDate = null,
            status = 0,
            statusName = null,
            lines = listOf(zeroLine)
        )
        val emptyLinesOrder = sampleOrder(
            orderNumber = 51,
            orderDate = null,
            shipDate = null,
            status = 0,
            statusName = "   ",
            lines = emptyList()
        )

        assertEquals(0, zeroValueOrder.itemCount)
        assertEquals(0.0, zeroValueOrder.itemsTotal, 0.001)
        assertFalse(zeroValueOrder.isShipped)
        assertEquals("—", formatOrderDate(zeroValueOrder.orderDate))
        assertEquals("Unknown Status", zeroValueOrder.customerFacingStatus)
        assertEquals(
            "Status details are currently unavailable.",
            zeroValueOrder.statusExplanation
        )

        assertEquals(0, emptyLinesOrder.itemCount)
        assertEquals(0.0, emptyLinesOrder.itemsTotal, 0.001)
        assertFalse(emptyLinesOrder.isShipped)
        assertEquals("Unknown Status", emptyLinesOrder.customerFacingStatus)
    }
}