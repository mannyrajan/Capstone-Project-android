package com.pnc.capstoneprojectandroid.features.products.data

data class ProductDetailsDto(
    val productId: Int,
    val productName: String?,
    val productNumber: String?,
    val summary: String?,
    val photo: String?,
    val photoFileName: String?,
    val color: String?,
    val listPrice: Double?,
    val warrantyPeriod: String?,
    val warrantyDescription: String?
)