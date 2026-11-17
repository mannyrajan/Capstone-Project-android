package com.pnc.capstone.ui.orderhistory

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pnc.capstone.data.model.CustomerOrder
import com.pnc.capstone.data.model.OrderLine
import com.pnc.capstone.data.model.formatMoney
import com.pnc.capstone.data.model.formatOrderDate

@Composable
fun OrderHistoryApp(viewModel: OrderHistoryViewModel = viewModel()) {
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
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order history") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                },
            )
        },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(padding).fillMaxSize(),
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Loading orders…")
                    }
                }

                state.orders.isEmpty() && state.errorMessage != null -> {
                    MessageBlock(
                        message = state.errorMessage,
                        actionLabel = "Try again",
                        onAction = onRefresh,
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            OutlinedTextField(
                                value = state.query,
                                onValueChange = onQueryChange,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = { Text("Search name, order #, or customer id") },
                            )
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ShipFilter.entries.forEach { filter ->
                                    FilterChip(
                                        selected = state.filter == filter,
                                        onClick = { onFilterChange(filter) },
                                        label = { Text(filter.name) },
                                    )
                                }
                            }
                        }
                        item {
                            Text(
                                "${state.visibleOrders.size} orders",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            state.errorMessage?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        }
                        if (state.visibleOrders.isEmpty()) {
                            item { Text("No orders match that search.") }
                        }
                        items(state.visibleOrders, key = { it.orderNumber }) { order ->
                            OrderCard(order = order, onClick = { onOpen(order) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: CustomerOrder, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "#${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                AssistChip(
                    onClick = {},
                    label = { Text(if (order.isShipped) "Shipped" else "Open") },
                )
            }
            Text(order.customerName, style = MaterialTheme.typography.bodyLarge)
            Text(
                "Ordered ${formatOrderDate(order.orderDate)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row {
                Text(
                    "${order.lines.size} lines · ${order.itemCount} items",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(formatMoney(order.itemsTotal), fontWeight = FontWeight.SemiBold)
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
                title = { Text("Order #${order.orderNumber}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(order.customerName, style = MaterialTheme.typography.headlineSmall)
                Text("Customer ${order.customerId}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Ordered ${formatOrderDate(order.orderDate)}")
                Text(
                    if (order.isShipped) "Shipped ${formatOrderDate(order.shipDate)}" else "Not shipped",
                )
            }
            item {
                if (state.detailLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
                state.detailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                state.shipping?.let { shipping ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Shipping", style = MaterialTheme.typography.titleMedium)
                            Text(shipping.shipMethod ?: "Ship method unavailable")
                            Text(
                                shipping.trackingNumber?.takeIf { it.isNotBlank() }
                                    ?: "No tracking number",
                            )
                            val phone = listOfNotNull(shipping.phone, shipping.phoneType?.let { "($it)" })
                                .joinToString(" ")
                            if (phone.isNotBlank()) Text(phone)
                            shipping.email?.let { Text(it) }
                            Text("Tax ${formatMoney(shipping.tax)}")
                            Text("Freight ${formatMoney(shipping.freight)}")
                        }
                    }
                }
            }
            item {
                Text("Items", style = MaterialTheme.typography.titleMedium)
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
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(line.productName, fontWeight = FontWeight.Medium)
                Text(
                    "Qty ${line.quantity} · ${formatMoney(line.unitPrice)} each",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(formatMoney(line.lineTotal), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MessageBlock(message: String, actionLabel: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onAction) { Text(actionLabel) }
    }
}

