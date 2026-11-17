package com.pnc.capstoneprojectandroid.features.orders.data

import com.pnc.capstoneprojectandroid.features.orders.domain.CustomerOrder
import com.pnc.capstoneprojectandroid.features.orders.domain.OrderLine
import com.pnc.capstoneprojectandroid.features.orders.domain.OrderRepository
import com.pnc.capstoneprojectandroid.features.orders.domain.OrderShipping
import com.pnc.capstoneprojectandroid.features.orders.domain.personName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultOrderRepository @Inject constructor(
    private val api: OrderApi
) : OrderRepository {
    override suspend fun getOrderHistory(): List<CustomerOrder> = withContext(Dispatchers.IO) {
        // calls run off the main thread
        api.getCustomerOrders()
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

    override suspend fun getShipping(lineId: Int): OrderShipping = withContext(Dispatchers.IO){
        // Offloading individual line detail fetches to Dispatchers.IO to maintain thread safety
        val detail = api.getCustomerOrder(lineId)
        OrderShipping(
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