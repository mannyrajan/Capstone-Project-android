package com.pnc.capstone.data.repository

import com.pnc.capstone.data.api.OrderApi
import com.pnc.capstone.data.model.CustomerOrder
import com.pnc.capstone.data.model.OrderLine
import com.pnc.capstone.data.model.OrderShipping
import com.pnc.capstone.data.model.personName

class OrderRepository(private val api: OrderApi) {
    suspend fun getOrderHistory(): List<CustomerOrder> {
        return api.getCustomerOrders()
            .groupBy { it.orderNumber }
            .map { (orderNumber, rows) ->
                val first = rows.first()
                CustomerOrder(
                    orderNumber = orderNumber,
                    customerId = first.customerId,
                    customerName = personName(
                        first.firstName,
                        first.middleName,
                        first.lastName,
                        first.suffix,
                    ),
                    orderDate = first.orderDate,
                    shipDate = first.shipDate,
                    representativeLineId = first.id,
                    lines = rows.map { row ->
                        OrderLine(
                            id = row.id,
                            productName = row.productName?.takeIf { it.isNotBlank() } ?: "Product",
                            quantity = row.orderQty,
                            unitPrice = row.unitPrice,
                            lineTotal = row.lineTotal,
                        )
                    },
                )
            }
            .sortedWith(
                compareByDescending<CustomerOrder> { it.orderDate }
                    .thenByDescending { it.orderNumber },
            )
    }

    suspend fun getShipping(lineId: Int): OrderShipping {
        val detail = api.getCustomerOrder(lineId)
        return OrderShipping(
            email = detail.emailAddress,
            phone = detail.phoneNumber,
            phoneType = detail.phoneNumberType,
            shipMethod = detail.shipMethodName,
            trackingNumber = detail.carrierTrackingNumber,
            freight = detail.freight,
            tax = detail.taxAmt,
        )
    }
}

