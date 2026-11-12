package com.pnc.capstoneprojectandroid.features.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.capstoneprojectandroid.features.products.domain.ProductsRepository
import com.pnc.capstoneprojectandroid.features.products.domain.SavedProductsRepository
import com.pnc.capstoneprojectandroid.features.products.presentation.state.SavedProductsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val savedRepository: SavedProductsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedProductsState())
    val uiState: StateFlow<SavedProductsState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                savedRepository.observeSavedIds().collect { ids ->
                    val products = ids.mapNotNull { id ->
                        runCatching { productsRepository.getProduct(id) }.getOrNull()
                    }
                    _uiState.update { it.copy(products = products, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Couldn't load saved products")
                }
            }
        }
    }
}

