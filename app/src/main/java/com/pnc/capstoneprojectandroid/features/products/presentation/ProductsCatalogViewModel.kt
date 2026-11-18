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
import retrofit2.HttpException
import java.io.IOException

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

            // setting loading state. if we have cached products, trigger a refresh
            // if the list is empty, trigger a full loading of data
            _uiState.update {
                it.copy(
                    isLoading = !hasProducts,
                    isRefreshing = hasProducts,
                    errorMessage = null // clear any previous error
                )
            }

            try {
                // fetch new products
                val products = repository.refreshProducts()
                // success state
                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }

            } catch (e: CancellationException) {
                // coroutine cancellation
                throw e
            } catch (e: HttpException) {
                // connectivity failure
                val message = if (e.code() in 500..599) {
                    "Server error: ${e.code()}"
                } else {
                    "Request failed: ${e.code()}"
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = message
                    )
                }
            } catch (e: IOException) {
                // connectivity failure
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = "Couldn't load products"
                    )
                }
            } catch (e: Exception) {
                // general safety net/catch all
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = "Something went wrong while processing data. Please try again."
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