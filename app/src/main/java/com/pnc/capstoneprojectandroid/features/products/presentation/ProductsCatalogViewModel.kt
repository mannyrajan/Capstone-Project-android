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
class ProductsCatalogViewModel @Inject constructor (
    private val repository: ProductsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(ProductCatalogState())
    val uiState: StateFlow<ProductCatalogState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                repository.observeProducts().collect { products ->
                    _uiState.update { it.copy(products = products, isLoading = false) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Couldn't load products"
                    )
                }
            }
        }
    }
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}