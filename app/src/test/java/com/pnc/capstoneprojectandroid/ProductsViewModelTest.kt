package com.pnc.capstoneprojectandroid

import com.pnc.capstoneprojectandroid.features.products.domain.Products
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Simple UI State representation for testing
sealed interface ProductUiState {
    object Loading : ProductUiState
    data class Success(val products: List<Products>) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

class TestProductsViewModel {
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    fun loadProducts(
        shouldFail: Boolean,
        products: List<Products> = listOf(
            Products(
                id = 1,
                name = "Test Bike",
                productNumber = "TB-01",
                price = 299.99,
                summary = "",
                thumbnailBase64 = "",
                warranty = "",
                color = ""
            )
        )
    ) {
        _uiState.value = ProductUiState.Loading
        if (shouldFail) {
            _uiState.value = ProductUiState.Error("Network error: Failed to fetch products")
        } else {
            _uiState.value = ProductUiState.Success(products)
        }
    }
}

class ProductsViewModelTest {

    @Test
    fun testViewModelLoadingToSuccessPath() = runTest {
        val viewModel = TestProductsViewModel()

        // Initial state should be loading
        assertEquals(ProductUiState.Loading, viewModel.uiState.value)

        // Trigger successful load
        viewModel.loadProducts(shouldFail = false)

        val currentState = viewModel.uiState.value
        assertTrue(currentState is ProductUiState.Success)
        assertEquals(1, (currentState as ProductUiState.Success).products.size)
        assertEquals("Test Bike", currentState.products[0].name)
    }

    @Test
    fun testViewModelEmptySuccessPath() = runTest {
        val viewModel = TestProductsViewModel()

        viewModel.loadProducts(shouldFail = false, products = emptyList())

        val currentState = viewModel.uiState.value
        assertTrue(currentState is ProductUiState.Success)
        assertTrue((currentState as ProductUiState.Success).products.isEmpty())
    }

    @Test
    fun testViewModelErrorFailurePath() = runTest {
        val viewModel = TestProductsViewModel()

        // Trigger failing load
        viewModel.loadProducts(shouldFail = true)

        val currentState = viewModel.uiState.value
        assertTrue(currentState is ProductUiState.Error)
        assertEquals(
            "Network error: Failed to fetch products",
            (currentState as ProductUiState.Error).message
        )
    }
}