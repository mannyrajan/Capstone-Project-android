package com.pnc.capstoneprojectandroid.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomerHomeScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    recentlyViewedProducts: List<Product> = MockData.sampleRecentlyViewed,
    onProductClick: (Int) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Welcome to Adventure Works",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.testTag("dashboard-title")
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

        // Summary of recent order activity Button
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

        // Recently viewed products carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Recently Viewed Products",
                    style = MaterialTheme.typography.titleMedium,
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentlyViewedProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id.toIntOrNull() ?: 0) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = product.price,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = product.stockStatus,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerHomePreview() {
    CustomerHomeScreen(
        onNavigateToProducts = {},
        onNavigateToOrders = {},
        recentlyViewedProducts = MockData.sampleRecentlyViewed,
        onProductClick = {}
    )
}