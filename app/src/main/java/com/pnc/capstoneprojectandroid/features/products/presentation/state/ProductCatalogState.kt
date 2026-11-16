package com.pnc.capstoneprojectandroid.features.products.presentation.state

import com.pnc.capstoneprojectandroid.features.products.domain.Products

// Defining explicit UI phases to the UI never guesses
sealed interface CatalogScreenState {
    object Loading: CatalogScreenState
    object Success: CatalogScreenState
    object EmptyCatalog: CatalogScreenState
    object NoSearchMatches: CatalogScreenState

    // explicit error types instead of raw strings
    object ConnectivityFailure: CatalogScreenState
    data class ServerError(val code: Int): CatalogScreenState
    object RequestTimeout: CatalogScreenState
    object InvalidResponseData: CatalogScreenState
}




data class ProductCatalogState (
    // core data and status
    val products: List<Products> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    // filter section
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