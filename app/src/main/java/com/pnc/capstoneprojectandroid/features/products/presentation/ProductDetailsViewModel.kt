
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val recentlyViewedRepository: RecentlyViewedRepository,
    private val inventoryRepository: InventoryRepository,
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
                val product = repository.getProduct(productId)

                _uiState.update {
                    it.copy(product = product, isLoading = false)
                }

                // Record the product in Recently Viewed.
                try {
                    recentlyViewedRepository.addRecentlyViewed(product)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // Product details should remain visible.
                }

                // Retrieve and classify inventory separately.
                try {
                    val inventory = inventoryRepository.getInventory()
                    val matchingItem = inventory.firstOrNull {
                        it.productId == product.id
                    }

                    val availability = if (matchingItem == null) {
                        ProductAvailability.UNKNOWN
                    } else {
                        ProductAvailabilityClassifier.classify(
                            quantity = matchingItem.quantity,
                            reorderPoint = matchingItem.reorderPoint
                        )
                    }

                    _uiState.update {
                        it.copy(availability = availability)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    _uiState.update {
                        it.copy(availability = ProductAvailability.UNKNOWN)
                    }
                }

            } catch (e: CancellationException) {
                // always rethrow cancellation
                throw e
            } catch (e: HttpException){
                // handle server error codes
                val message = if (e.code() in 500..599) {
                    "Server error: ${e.code()}"
                } else {
                    "Request failed: ${e.code()}"
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
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
                    it.copy(isLoading = false,
                            errorMessage = "Something went wrong while processing data. Please try again.")
                }
            }
        }
    }
}
