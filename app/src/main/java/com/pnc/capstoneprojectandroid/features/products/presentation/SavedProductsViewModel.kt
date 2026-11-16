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
import kotlin.coroutines.cancellation.CancellationException

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
                // collective reactive stream of saved product ids
                savedRepository.observeSavedIds().collect { ids ->
                    val products = ids.mapNotNull { id ->
                        runCatching { productsRepository.getProduct(id) }.getOrNull()
                    }
                    _uiState.update {
                        it.copy(
                        products = products,
                        isLoading = false,
                        errorMessage = null
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Something went wrong while processing data. Please try again.")
                }
            }
        }
    }
}

