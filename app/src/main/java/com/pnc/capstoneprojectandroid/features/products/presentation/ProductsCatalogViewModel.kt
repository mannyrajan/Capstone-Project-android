package com.pnc.capstoneprojectandroid.features.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.capstoneprojectandroid.features.products.domain.ProductsRepository
import com.pnc.capstoneprojectandroid.features.products.presentation.state.ProductCatalogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ProductsCatalogViewModel @Inject constructor(
    private val repository: ProductsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductCatalogState())
    val uiState: StateFlow<ProductCatalogState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {

        // Prevent multiple requests from running simultaneously
        if (_uiState.value.isLoading || _uiState.value.isRefreshing) return

        viewModelScope.launch {

            val hasProducts = _uiState.value.products.isNotEmpty()

            _uiState.update {
                it.copy(
                    isLoading = !hasProducts,
                    isRefreshing = hasProducts,
                    errorMessage = null
                )
            }

            try {
                val products = repository.refreshProducts()

                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = e.message ?: "Couldn't load products"
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Added this method to toggle or set the filter visibility
    fun setFilterVisibility(isVisible: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(showFilter = isVisible)
        }
    }

    // adding filters in the viewmodel
    // filter by color
    fun updateColorFilter(color: String) {
        _uiState.update { currentState ->
            currentState.copy(colorFilter = color)
        }
    }

    // filter by price
    fun updatePriceFilter(price: Double) {
        _uiState.update { currentState ->
            currentState.copy(priceFilter = price)
        }
    }

    // filter by warranty
    fun updateWarrantyFilter(warranty: String) {
        _uiState.update { currentState ->
            currentState.copy(warrantyFilter = warranty)
        }
    }

    fun clearFilters() {
        _uiState.update { currentState ->
            currentState.copy(
                colorFilter = "",
                priceFilter = null,
                warrantyFilter = ""
            )
        }
    }


}