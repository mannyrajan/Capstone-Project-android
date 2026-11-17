package com.pnc.capstoneprojectandroid.features.products.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnc.capstoneprojectandroid.features.products.presentation.ProductsCatalogViewModel
import androidx.compose.material.icons.filled.Refresh

@Composable
fun ProductsScreen(
    onProductClick: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductsCatalogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {

        when {
            // Initial loading
            state.isLoading && state.products.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .semantics { contentDescription = "Loading products" }
                )
            }

            // Error when no products are available
            state.errorMessage != null && state.products.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = state.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )

                    Button(onClick = { viewModel.loadProducts() }) {
                        Text("Retry")
                    }
                }
            }

            // Display products, including while refreshing
            else -> {
                ProductsList(
                    filteredProducts = state.filteredProducts,
                    onProductClick = onProductClick,
                    onBack = onBack,
                    onRefresh = { viewModel.loadProducts() },
                    isRefreshing = state.isRefreshing,
                    onShowFilterChange = { showFilter ->
                        viewModel.setFilterVisibility(showFilter)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Small progress indicator during refresh
        if (state.isRefreshing && state.products.isNotEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp)
                    .semantics { contentDescription = "Refreshing products" }
            )
        }

        // Keep existing products visible if refreshing fails
        if (state.errorMessage != null && state.products.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp)
                    .semantics {
                        isTraversalGroup = true
                        traversalIndex = -1f
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )

                Button(onClick = { viewModel.loadProducts() }) {
                    Text("Retry")
                }
            }
        }
        // Modular call to the filter bottom sheet component
        if (state.showFilter) {
            ProductFilterSheet(
                state = state,
                onColorChange = { viewModel.updateColorFilter(it) },
                onWarrantyChange = { viewModel.updateWarrantyFilter(it) },
                onPriceChange = { viewModel.updatePriceFilter(it) },
                onClearFilters = { viewModel.clearFilters() },
                onDismiss = { viewModel.setFilterVisibility(false) }
            )
        }
    }
}
