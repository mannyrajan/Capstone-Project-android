package com.pnc.capstoneprojectandroid.features.products.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.capstoneprojectandroid.features.products.domain.ProductsRepository
import com.pnc.capstoneprojectandroid.features.products.domain.RecentlyViewedRepository
import com.pnc.capstoneprojectandroid.features.products.presentation.state.ProductDetailsState
import com.pnc.capstoneprojectandroid.features.inventory.data.repository.InventoryRepository
import com.pnc.capstoneprojectandroid.features.inventory.domain.ProductAvailability
import com.pnc.capstoneprojectandroid.features.inventory.domain.ProductAvailabilityClassifier
import com.pnc.capstoneprojectandroid.features.products.domain.SavedProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException
import java.io.IOException
import java.io.InterruptedIOException
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val recentlyViewedRepository: RecentlyViewedRepository,
    private val inventoryRepository: InventoryRepository,
    private val savedProductsRepository: SavedProductsRepository, // <-- Injecting saved products
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow(ProductDetailsState())
    val uiState: StateFlow<ProductDetailsState> = _uiState.asStateFlow()

    init {
        load()
        observeSavedStatus()
    }

    private fun observeSavedStatus() {
        viewModelScope.launch {
            savedProductsRepository.observeSavedIds().collect { savedIds ->
                _uiState.update { it.copy(isSaved = productId in savedIds) }
            }
        }
    }

    fun toggleSaved() {
        val currentProduct = _uiState.value.product ?: return
        viewModelScope.launch {
            savedProductsRepository.toggleSaved(currentProduct)
        }
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                supervisorScope {
                    launch { loadAvailability() }

                    val product = repository.getProduct(productId)

                    _uiState.update {
                        it.copy(product = product, isLoading = false)
                    }

                    // Record the product in Recently Viewed.
                    launch {
                        try {
                            recentlyViewedRepository.addRecentlyViewed(product)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: Exception) {
                            // Product details should remain visible.
                        }
                    }
                }
            } catch (e: CancellationException) {
                // always rethrow cancellation
                throw e
            } catch (e: HttpException) {
                // handle server error codes
                val message = if (e.code() in 500..599) {
                    "Server error: ${e.code()}"
                } else {
                    "Request failed: ${e.code()}"
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            } catch (e: InterruptedIOException) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "The request timed out. Please try again.")
                }
            } catch (e: IOException) {
                // handle network errors
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No internet connection. Please check your network settings and try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Something went wrong while processing data. Please try again."
                    )
                }
            }

        }
    }

    private suspend fun loadAvailability() {
        val availability = try {
            val inventory = withTimeoutOrNull(5_000) { inventoryRepository.getInventory() }
            val matchingItem = inventory?.firstOrNull { it.productId == productId }
            if (matchingItem == null) {
                ProductAvailability.UNKNOWN
            } else {
                ProductAvailabilityClassifier.classify(
                    quantity = matchingItem.quantity,
                    reorderPoint = matchingItem.reorderPoint
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            ProductAvailability.UNKNOWN
        }
        _uiState.update { it.copy(availability = availability) }
    }
}
