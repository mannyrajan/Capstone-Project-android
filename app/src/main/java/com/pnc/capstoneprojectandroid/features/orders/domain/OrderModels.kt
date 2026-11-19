package com.pnc.capstoneprojectandroid.features.orders.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class OrderLine(
    val id: Int,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val lineTotal: Double,
)

data class CustomerOrder(
    val orderNumber: Int,
    val customerId: Int,
    val customerName: String,
    val orderDate: String?,
    val shipDate: String?,
    val status: Int, // added from api schema, status code for the order
    val statusName: String?, // added from api schema, name of the status
    val representativeLineId: Int,
    val lines: List<OrderLine>,
) {
    val itemsTotal: Double get() = lines.sumOf { it.lineTotal }
    val itemCount: Int get() = lines.sumOf { it.quantity }
    val isShipped: Boolean get() = !shipDate.isNullOrBlank()

    // customer facing status string adhering to requirement 12
    val customerFacingStatus: String
        get() = OrderStatusClassifier.displayLabelFor(status, statusName)

    // customer explantation of order status
    val statusExplanation: String
        get() = OrderStatusClassifier.explanationFor(status, statusName)
}

data class OrderShipping(
    val email: String?,
    val phone: String?,
    val phoneType: String?,
    val shipMethod: String?,
    val trackingNumber: String?,
    val freight: Double,
    val tax: Double,
)

// status classifier and fallback
enum class OrderStatus {
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    UNKNOWN,
}

object OrderStatusClassifier {
    fun classify(statusCode: Int?, statusName: String?): OrderStatus {
        // check numeric status code first
        when (statusCode) {
            1,2 -> return OrderStatus.PROCESSING
            5 -> return OrderStatus.SHIPPED
            6 -> return OrderStatus.DELIVERED
            7 -> return OrderStatus.CANCELLED
        }
        // fallback to status name
        return when (statusName?.trim()?.uppercase()) {
            "PROCESSING", "PENDING", "OPEN" -> OrderStatus.PROCESSING
            "SHIPPED" -> OrderStatus.SHIPPED
            "DELIVERED" -> OrderStatus.DELIVERED
            "CANCELLED", "CANC" -> OrderStatus.CANCELLED
            else -> OrderStatus.UNKNOWN // safe fall back
    }
}
    fun displayLabel(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PROCESSING -> "Processing"
            OrderStatus.SHIPPED -> "Shipped"
            OrderStatus.DELIVERED -> "Delivered"
            OrderStatus.CANCELLED -> "Cancelled"
            OrderStatus.UNKNOWN -> "Unknown Status"
        }
    }
    fun displayLabelFor(statusCode: Int?, statusName: String?): String {
        return displayLabel(classify(statusCode, statusName))
    }

    fun explanation(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PROCESSING -> "Your order is being prepared."
            OrderStatus.SHIPPED -> "Your order is on the way."
            OrderStatus.DELIVERED -> "Your order has been delivered."
            OrderStatus.CANCELLED -> "This order was cancelled."
            OrderStatus.UNKNOWN -> "Status details are currently unavailable."
        }
    }

    fun explanationFor(statusCode: Int?, statusName: String?): String {
        return explanation(classify(statusCode, statusName))
    }
}


fun personName(first: String?, middle: String?, last: String?, suffix: String?): String {
    return listOf(first, middle, last, suffix)
        .mapNotNull { it?.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" ")
        .ifBlank { "Unknown customer" }
}

private val orderDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)

fun formatOrderDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "—"
    return try {
        LocalDateTime.parse(raw).format(orderDateFormatter)
    } catch (_: Exception) {
        raw.take(10)
    }
}

fun formatMoney(amount: Double): String = String.format(Locale.US, "$%,.2f", amount)
