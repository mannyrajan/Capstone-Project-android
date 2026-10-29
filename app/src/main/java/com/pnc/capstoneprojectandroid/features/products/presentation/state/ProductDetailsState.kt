package com.pnc.capstoneprojectandroid.features.products.presentation.state

import com.pnc.capstoneprojectandroid.features.products.domain.Products

data class ProductDetailsState(
    val product: Products? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)