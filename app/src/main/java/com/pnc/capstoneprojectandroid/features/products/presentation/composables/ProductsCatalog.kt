package com.pnc.capstoneprojectandroid.features.products.presentation.composables

import android.util.Base64
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pnc.capstoneprojectandroid.features.products.domain.Products
import kotlin.let
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.paint
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.pnc.capstoneprojectandroid.features.products.presentation.state.ProductCatalogState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Composable
fun ProductsList(
    // changed input from raw List<Products> to accept the filtered list from state
    filteredProducts: List<Products>,
    onProductClick: (Int) -> Unit,
    onShowFilterChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    // moving listState and scope so the Scaffold can control the Back-to-Top button action
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }

    // Case-insensitive filtering based on product name or product number
    val displayedProducts = remember(searchQuery, filteredProducts) {
        if (searchQuery.isBlank()) {
            filteredProducts
        } else {
            filteredProducts.filter { product ->
                product.name.contains(searchQuery.trim(), ignoreCase = true) ||
                        product.productNumber.contains(searchQuery.trim(), ignoreCase = true)
            }
        }
    }
Scaffold(
    modifier = modifier.fillMaxSize(),
    floatingActionButton = {
        // back to top arrow button
        AnimatedVisibility(visible = showButton) {
            FloatingActionButton(
                onClick = {scope.launch { listState.animateScrollToItem(0) }}
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Back to top"
                )
            }
        }
    }
) { innerPadding ->
   Column(modifier = Modifier
       .fillMaxSize()
       .padding(innerPadding)
   ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Products",
                    style = typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    // removed due to it pushing the button of the screen
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp)
                )
                IconButton(onClick = { onShowFilterChange(true) } ) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "Show Filter")
                }
            }
        }

        // Search Bar TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            placeholder = { Text("Search products...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) { // Clears and restores catalog
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )


        ProductsListBody(
            products = displayedProducts,
            searchQuery = searchQuery,
            onProductClick = onProductClick,
            listState = listState,
            modifier = Modifier.weight(1f)
        )

    } // end of Column

} // end of Scaffold

}// end of fun





@Composable
private fun ProductsListBody(
    products: List<Products>,
    searchQuery: String, // New parameter for search
    onProductClick: (Int) -> Unit,
    listState:  androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (products.isEmpty() && searchQuery.isNotBlank()) {
            // Empty state communication
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No products match \"$searchQuery\"",
                    style = typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(products, key = { it.id }) { product ->
                    ProductRow(
                        product = product,
                        onClick = { onProductClick(product.id) }
                    )
                    HorizontalDivider()
                }
            }
        }

    }
}

@Composable
private fun ProductRow(
    product: Products,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = product.productNumber,
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ProductThumbnail(
            base64 = product.thumbnailBase64,
            contentDescription = product.name
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProductThumbnail(
    base64: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val bytes = remember(base64) {
        base64?.let {
            runCatching {
                Base64.decode(it.substringAfter("base64,"), Base64.DEFAULT)
            }.getOrNull()
        }
    }

    val shape = RoundedCornerShape(8.dp)

    if (bytes != null) {
        AsyncImage(
            model = bytes,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(shape)
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}
