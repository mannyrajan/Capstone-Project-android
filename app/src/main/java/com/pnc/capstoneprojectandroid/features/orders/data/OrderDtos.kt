package com.pnc.capstoneprojectandroid.features.orders.data

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

// moved code over to domain layer for consistency
