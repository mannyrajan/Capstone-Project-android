package com.pnc.capstoneprojectandroid.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnc.capstoneprojectandroid.features.products.domain.Products
import com.pnc.capstoneprojectandroid.features.products.presentation.RecentlyViewedViewModel
import com.pnc.capstoneprojectandroid.features.products.presentation.SavedProductsViewModel
import com.pnc.capstoneprojectandroid.features.products.presentation.composables.ProductThumbnail
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.heightIn

@Composable
fun CustomerHomeScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onProductClick: (Int) -> Unit = {},
    onNavigateToSaved: () -> Unit = {}
) {
    val recentlyViewedViewModel: RecentlyViewedViewModel = hiltViewModel()
    val recentlyViewedProducts by recentlyViewedViewModel.recentlyViewedProducts.collectAsStateWithLifecycle()
    val clearError by recentlyViewedViewModel.errorMessage.collectAsStateWithLifecycle()

    val savedViewModel: SavedProductsViewModel = hiltViewModel()
    val savedState by savedViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Welcome to Adventure Works",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard-title")
                        .semantics { heading() }
                )
            }
            // Product Catalog Button
            item {
                Button(
                    onClick = onNavigateToProducts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("view-product-catalog-button")
                ) {
                    Text("View Product Catalog")
                }
            }

            // Recent Orders Button
            item {
                Button(
                    onClick = onNavigateToOrders,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("view-recent-orders-button")
                ) {
                    Text("View Recent Orders")
                }
            }
            // Recently Viewed Products Carousel
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recently Viewed Products",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(1f)
                                .semantics { heading() }
                        )
                        AnimatedVisibility(
                            visible = recentlyViewedProducts.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            TextButton(
                                onClick = { recentlyViewedViewModel.clearHistory() },
                                modifier = Modifier.semantics {
                                    contentDescription = "Clear recently viewed products"
                                }
                            ) {
                                Text("Clear")
                            }
                        }
                    }

                    clearError?.let { message ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics { liveRegion = LiveRegionMode.Polite }
                            )
                            TextButton(onClick = { recentlyViewedViewModel.dismissError() }) {
                                Text("Dismiss")
                            }
                        }
                    }

                    AnimatedContent(
                        targetState = recentlyViewedProducts,
                        contentKey = { it.isEmpty() },
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(200))
                        },
                        label = "recentlyViewedCarousel"
                    ) { products ->
                        if (products.isEmpty()) {
                            Text(
                                text = "No recently viewed products yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.semantics {
                                    liveRegion = LiveRegionMode.Polite
                                }
                            )
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = products,
                                    key = { it.id }
                                ) { product ->
                                    ProductCard(
                                        product = product,
                                        onClick = { onProductClick(product.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Saved Products Carousel
            item {
                SavedProductsCarousel(
                    products = savedState.products,
                    errorMessage = savedState.errorMessage,
                    onDismissError = { savedViewModel.dismissError() },
                    onProductClick = onProductClick,
                    onViewAll = onNavigateToSaved,
                    onUnsave = { product -> savedViewModel.toggleSaved(product) }
                )
            }
        }
    }
}

@Composable
fun SavedProductsCarousel(
    products: List<Products>,
    errorMessage: String?,
    onDismissError: () -> Unit,
    onProductClick: (Int) -> Unit,
    onViewAll: () -> Unit,
    onUnsave: (Products) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Saved Products",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() }
            )
            AnimatedVisibility(
                visible = products.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TextButton(onClick = onViewAll) {
                    Text("View all")
                }
            }
        }
        errorMessage?.let { message ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { liveRegion = LiveRegionMode.Polite }
                )
                TextButton(onClick = onDismissError) {
                    Text("Dismiss")
                }
            }
        }

        AnimatedContent(
            targetState = products,
            contentKey = { it.isEmpty() },
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(200))
            },
            label = "savedProductsCarousel"
        ) { savedProducts ->
            if (savedProducts.isEmpty()) {
                Text(
                    text = "No saved products yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics {
                        liveRegion = LiveRegionMode.Polite
                    }
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(savedProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            modifier = Modifier.animateItem(),
                            onUnsave = { onUnsave(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Products,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onUnsave: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .clickable(onClickLabel = "View details", onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(
                    start = 10.dp,
                    top = if (onUnsave != null) 34.dp else 10.dp,
                    end = 10.dp,
                    bottom = 10.dp
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProductThumbnail(
                        base64 = product.thumbnailBase64,
                        contentDescription = product.name,
                        size = 90.dp
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale.US).format(product.price),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = product.productNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (onUnsave != null) {
                IconButton(
                    onClick = onUnsave,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Remove ${product.name} from saved products",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
