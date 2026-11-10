package com.pnc.capstoneprojectandroid.features.products.presentation.state

import com.pnc.capstoneprojectandroid.features.products.domain.Products

data class ProductCatalogState (
    val products: List<Products> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showFilter: Boolean = false,
    val colorFilter: String = "",
    val priceFilter: Double? = null,
    val warrantyFilter: String = ""
){
    val filteredProducts: List<Products>
        get() = products.filter {
            val matchesColor = colorFilter == "" || it.color.contains(colorFilter, ignoreCase = true)
            val matchesPrice = priceFilter == null || it.price <= priceFilter
            val matchesWarranty = warrantyFilter == "" || it.warranty.contains(warrantyFilter, ignoreCase = true)

            matchesColor && matchesPrice && matchesWarranty
        }

    // getting values from the products list to filter
    val availableColors: List<String>
        get() = products.map { it.color }.filter { it.isNotBlank() }.distinct().sorted()

    val availableWarranties: List<String>
        get() = products.map { it.warranty }.filter { it.isNotBlank() }.distinct().sorted()



}