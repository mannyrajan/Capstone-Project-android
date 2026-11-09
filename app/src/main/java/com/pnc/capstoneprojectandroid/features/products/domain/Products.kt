package com.pnc.capstoneprojectandroid.features.products.domain

data class Products (
    val id: Int,
    val name: String,
    val productNumber: String,
    val summary: String,
    val thumbnailBase64: String?,
    val warranty: String,
    val color: String,
    val price: Double
)