package com.pnc.capstoneprojectandroid.features.orders.data.model

import androidx.annotation.Keep
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Keep
data class CustomerOrderLineDto(
    val id: Int,
    val customerId: Int,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val suffix: String?,
    val orderDate: String?,
    val shipDate: String?,
    val productName: String?,
    val orderNumber: Int,
    val orderQty: Int,
    val unitPrice: Double,
    val lineTotal: Double,
)

@Keep
data class CustomerOrderDetailDto(
    val id: Int,
    val customerId: Int,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val suffix: String?,
    val phoneNumber: String?,
    val phoneNumberType: String?,
    val emailAddress: String?,
    val orderNumber: Int,
    val orderDate: String?,
    val shipDate: String?,
    val freight: Double,
    val shipMethodName: String?,
    val taxAmt: Double,
    val productName: String?,
    val carrierTrackingNumber: String?,
    val orderQty: Int,
    val unitPrice: Double,
    val lineTotal: Double,
)

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
    val representativeLineId: Int,
    val lines: List<OrderLine>,
) {
    val itemsTotal: Double get() = lines.sumOf { it.lineTotal }
    val itemCount: Int get() = lines.sumOf { it.quantity }
    val isShipped: Boolean get() = !shipDate.isNullOrBlank()
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

