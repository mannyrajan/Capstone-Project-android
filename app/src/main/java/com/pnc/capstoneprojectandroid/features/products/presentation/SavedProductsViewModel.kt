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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            savedRepository.observeSaved()
                .catch { e ->
                    if (e is CancellationException) throw e
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Couldn't load saved products"
                        )
                    }
                }
                .collect { products ->
                    _uiState.update {
                        it.copy(products = products, isLoading = false, errorMessage = null)
                    }
                }
        }
    }
    fun toggleSaved(product: Products) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }

            try {
                savedRepository.toggleSaved(product)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Couldn't update saved products")
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}