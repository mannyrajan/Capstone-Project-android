package com.pnc.capstoneprojectandroid.features.products.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnc.capstoneprojectandroid.features.products.presentation.SavedProductsViewModel
import java.text.NumberFormat
import java.util.Locale


// Added statusBarsPadding to clear the device notch and camera punch-hole correctly.
// Wrapped dynamic states inside a weight(1f) container to keep the header pinned and unblock click events.
@Composable
fun SavedProductsScreen(
    onProductClick: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedProductsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Main layout container respecting window insets and top status bar padding
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Back button and screen title header row (Pinned at top)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Saved Products",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Flexible container ensuring loading, empty, and list states do not overlap header or block clicks
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Loading state indicator while fetching persisted records from Room
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .semantics { contentDescription = "Loading products" }
                    )
                }

                // Error state with retry option if database observation encounters issues
                state.errorMessage != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = state.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { viewModel.load() }) {
                            Text("Retry")
                        }
                    }
                }

                // Empty state when no products have been favorited yet
                state.products.isEmpty() -> {
                    Text(
                        text = "No saved products yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                // Success state rendering the list of saved items backed by Room
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.products, key = { it.id }) { product ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onProductClick(product.id) }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = NumberFormat.getCurrencyInstance(Locale.US)
                                            .format(product.price),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                ProductThumbnail(
                                    base64 = product.thumbnailBase64,
                                    contentDescription = product.name
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}