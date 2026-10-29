package com.pnc.capstoneprojectandroid.features.products.presentation.state

import com.pnc.capstoneprojectandroid.features.products.domain.Products

data class ProductCatalogState (
    val products: List<Products> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)