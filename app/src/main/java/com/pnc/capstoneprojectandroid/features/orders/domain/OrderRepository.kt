package com.pnc.capstoneprojectandroid.features.orders.domain


interface OrderRepository {
    suspend fun getOrderHistory(): List<CustomerOrder>
    suspend fun getShipping(lineId: Int): OrderShipping
}