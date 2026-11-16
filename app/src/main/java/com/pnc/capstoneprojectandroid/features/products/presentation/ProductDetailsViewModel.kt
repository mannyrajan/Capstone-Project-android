
package com.pnc.capstoneprojectandroid.features.products.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.capstoneprojectandroid.features.products.domain.ProductsRepository
import com.pnc.capstoneprojectandroid.features.products.domain.RecentlyViewedRepository
import com.pnc.capstoneprojectandroid.features.products.presentation.state.ProductDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val recentlyViewedRepository: RecentlyViewedRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow(ProductDetailsState())
    val uiState: StateFlow<ProductDetailsState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                // Load product details first.
                val product = repository.getProduct(productId)

                // Display the successfully loaded product.
                _uiState.update {
                    it.copy(product = product, isLoading = false)
                }

                // Record it in Recently Viewed.
                // A history-saving failure should not break product details.
                try {
                    recentlyViewedRepository.addRecentlyViewed(product)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // Keep the successfully loaded product visible.
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Couldn't load product"
                    )
                }
            }
        }
    }
}
