package com.pnc.capstoneprojectandroid.features.orders.presentation.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pnc.capstoneprojectandroid.features.orders.domain.CustomerOrder
import com.pnc.capstoneprojectandroid.features.orders.domain.OrderLine
import com.pnc.capstoneprojectandroid.features.orders.domain.formatMoney
import com.pnc.capstoneprojectandroid.features.orders.domain.formatOrderDate
import com.pnc.capstoneprojectandroid.features.orders.presentation.OrderHistoryUiState
import com.pnc.capstoneprojectandroid.features.orders.presentation.OrderHistoryViewModel
import com.pnc.capstoneprojectandroid.features.orders.presentation.ShipFilter
import androidx.compose.material3.TextButton

@Composable
fun OrderHistoryApp(
    viewModel: OrderHistoryViewModel = hiltViewModel(), // injecting the viewmodel via hilt
    onBack: () -> Unit // adding callback to go back to customer home page
) {
    val state by viewModel.state.collectAsState()
    BackHandler(enabled = state.selected != null) { viewModel.closeOrder() }

    val selected = state.selected
    if (selected == null) {
        OrderListScreen(
            state = state,
            onRefresh = { viewModel.refresh() },
            onQueryChange = viewModel::onQueryChange,
            onFilterChange = viewModel::onFilterChange,
            onOpen = viewModel::openOrder,
            onBack = onBack
        )
    } else {
        OrderDetailScreen(
            order = selected,
            state = state,
            onBack = viewModel::closeOrder,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderListScreen(
    state: OrderHistoryUiState,
    onRefresh: () -> Unit,
    onQueryChange: (String) -> Unit,
    onFilterChange: (ShipFilter) -> Unit,
    onOpen: (CustomerOrder) -> Unit,
    onBack: () -> Unit // added callback to go back to customer home page
) {
    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Order history") },
//                actions = {
//                    IconButton(onClick = onRefresh) {
//                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
//                    }
//                },
//            )
//        },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics { isTraversalGroup = true },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.semantics {
                                contentDescription = "Loading orders"
                            }
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Loading orders…",
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                            }
                        )
                    }
                }

                state.orders.isEmpty() && state.errorMessage != null -> {
                    MessageBlock(
                        message = state.errorMessage,
                        actionLabel = "Try again",
                        onAction = onRefresh,
                        onBack = onBack
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics { isTraversalGroup = true },
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // adding in custom blue header
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(
                                        onClick = onBack,
                                        modifier = Modifier.semantics {
                                            contentDescription = "Back to Home"
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    Text(
                                        text = "Order History",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                    IconButton(
                                        onClick = onRefresh,
                                        modifier = Modifier.semantics {
                                            contentDescription = "Refresh order history"
                                        }
                                    ) {
                                        Icon(
                                            Icons.Filled.Refresh,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }

                                }

                            }
                        }
                        item {
                            OutlinedTextField(
                                value = state.query,
                                onValueChange = onQueryChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "Search orders by order number"
                                    },
                                singleLine = true,
                                label = { Text("Search by order number") },
                            )
                        }
                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Filter orders by shipping status",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.semantics { heading() }
                                )
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ShipFilter.entries.forEach { filter ->
                                        val selectedFilter = state.filter == filter
                                        FilterChip(
                                            selected = selectedFilter,
                                            onClick = { onFilterChange(filter) },
                                            label = {
                                                Text(filter.name)
                                            },
                                            modifier = Modifier.semantics {
                                                contentDescription = "${filter.name} orders filter"
                                                stateDescription = if (selectedFilter) {
                                                    "Selected"
                                                } else {
                                                    "Not selected"
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "${state.visibleOrders.size} orders",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.semantics {
                                        liveRegion = LiveRegionMode.Polite
                                    }
                                )
                                state.errorMessage?.let {
                                    Text(
                                        it,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.semantics {
                                            liveRegion = LiveRegionMode.Polite
                                        }
                                    )
                                    Button(onClick = onRefresh) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                        if (state.visibleOrders.isEmpty()) {
                            item {
                                Text(
                                    "No orders match that search.",
                                    modifier = Modifier.semantics {
                                        liveRegion = LiveRegionMode.Polite
                                    }
                                )
                            }
                        }
                        items(state.visibleOrders, key = { it.orderNumber }) { order ->
                            OrderCard(order = order, onClick = { onOpen(order) })
                        }
                    }
                }
            }
            if (state.isRefreshing && state.orders.isNotEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                        .semantics { contentDescription = "Refreshing orders" }
                )
            }
        }
    }
}

@Composable
private fun OrderCard(order: CustomerOrder, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {
                contentDescription = "Order ${order.orderNumber}, ${order.customerName}, " +
                        "${order.customerFacingStatus}, " +
                        "${order.itemCount} items, " +
                        "total ${formatMoney(order.itemsTotal)}"
            }
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "#${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = { Text(order.customerFacingStatus) },
                )
            }
            Text(order.customerName, style = MaterialTheme.typography.bodyLarge)
            Text(
                "Ordered ${formatOrderDate(order.orderDate)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${order.itemCount} items",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    formatMoney(order.itemsTotal),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailScreen(
    order: CustomerOrder,
    state: OrderHistoryUiState,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Order #${order.orderNumber}",
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics {
                            contentDescription = "Back to order history"
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .semantics { isTraversalGroup = true },
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        order.customerName,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text("Ordered ${formatOrderDate(order.orderDate)}")
                    Text(
                        if (order.isShipped) {
                            "Shipped ${formatOrderDate(order.shipDate)}"
                        } else {
                            "Not shipped"
                        },
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.detailLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription = "Loading shipping details"
                                }
                        )
                    }
                    state.detailError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                            }
                        )
                    }
                    state.shipping?.let { shipping ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    "Shipping",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.semantics { heading() }
                                )

                                shipping.shipMethod?.takeIf { it.isNotBlank() }?.let {
                                    Text("Method: $it")
                                }
                                shipping.trackingNumber?.takeIf { it.isNotBlank() }?.let {
                                    Text("Tracking: $it")
                                } ?: Text("Tracking: Not available yet")

                                val phone = listOfNotNull(
                                    shipping.phone?.takeIf { it.isNotBlank() },
                                    shipping.phoneType?.takeIf { it.isNotBlank() }?.let { "($it)" }
                                ).joinToString(" ")
                                if (phone.isNotBlank()) Text("Phone: $phone")

                                shipping.email?.takeIf { it.isNotBlank() }?.let {
                                    Text("Email: $it")
                                }

                                if (shipping.tax > 0) Text("Tax: ${formatMoney(shipping.tax)}")
                                if (shipping.freight > 0) Text("Freight: ${formatMoney(shipping.freight)}")
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    "Items",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
            }
            items(order.lines, key = { it.id }) { line ->
                LineRow(line)
            }
            item {
                Text(
                    "Items total ${formatMoney(order.itemsTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun LineRow(line: OrderLine) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "${line.productName}, quantity ${line.quantity}, " +
                        "${formatMoney(line.unitPrice)} each, " +
                        "line total ${formatMoney(line.lineTotal)}"
            }
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(line.productName, fontWeight = FontWeight.Medium)
                Text(
                    "Qty ${line.quantity} · ${formatMoney(line.unitPrice)} each",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                formatMoney(line.lineTotal),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MessageBlock(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .semantics { isTraversalGroup = true },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            message,
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
            }
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = onAction) {
            Text(actionLabel)
        }
        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}