package com.pnc.capstoneprojectandroid.features.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.capstoneprojectandroid.features.products.domain.Products
import com.pnc.capstoneprojectandroid.features.products.domain.SavedProductsRepository
import com.pnc.capstoneprojectandroid.features.products.presentation.state.SavedProductsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var loadJob: Job? = null

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }
            savedRepository.observeSaved()
                .catch {
                    _uiState.update { state ->
                        state.copy(isLoading = false, errorMessage = "Couldn't load saved products")
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
            try {
                savedRepository.toggleSaved(product)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Couldn't update saved products") }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}