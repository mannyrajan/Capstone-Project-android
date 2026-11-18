package com.pnc.capstoneprojectandroid.features.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.capstoneprojectandroid.features.products.domain.Products
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
    private val savedRepository: SavedProductsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedProductsState())
    val uiState: StateFlow<SavedProductsState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            savedRepository.observeSaved().collect { products ->
                _uiState.update {
                    it.copy(products = products, isLoading = false, errorMessage = null)
                }
            }
        }
    }

    fun toggleSaved(product: Products) {
        viewModelScope.launch {
            savedRepository.toggleSaved(product)
        }
    }
}