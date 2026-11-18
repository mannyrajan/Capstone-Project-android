package com.pnc.capstoneprojectandroid.features.products.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.pnc.capstoneprojectandroid.features.products.presentation.state.ProductCatalogState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductFilterSheet(
    state: ProductCatalogState,
    onColorChange: (String) -> Unit,
    onWarrantyChange: (String) -> Unit,
    onPriceChange: (Double) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Filter Products",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { heading() }
                )

                // 1. Color Filter Chips (Dynamically populated from state)
                if (state.availableColors.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Color",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.semantics { heading() }
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.availableColors.forEach { color ->
                                val selected = state.colorFilter.equals(color, ignoreCase = true)
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        val newColor = if (state.colorFilter.equals(
                                                color,
                                                ignoreCase = true
                                            )
                                        ) "" else color
                                        onColorChange(newColor)
                                    },
                                    label = { Text(color) },
                                    modifier = Modifier.semantics {
                                        contentDescription = "$color color filter"
                                        stateDescription =
                                            if (selected) "Selected" else "Not selected"
                                    },
                                    leadingIcon = if (selected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Done,
                                                contentDescription = null,
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                }

                // 2. Warranty Filter Chips (Dynamically populated from state)
                if (state.availableWarranties.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Warranty",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.semantics { heading() }
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.availableWarranties.forEach { warranty ->
                                val selected =
                                    state.warrantyFilter.equals(warranty, ignoreCase = true)
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        val newWarranty = if (state.warrantyFilter.equals(
                                                warranty,
                                                ignoreCase = true
                                            )
                                        ) "" else warranty
                                        onWarrantyChange(newWarranty)
                                    },
                                    label = { Text(warranty) },
                                    modifier = Modifier.semantics {
                                        contentDescription = "$warranty warranty filter"
                                        stateDescription =
                                            if (selected) "Selected" else "Not selected"
                                    },
                                    leadingIcon = if (selected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Done,
                                                contentDescription = null,
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                }

                // 3. Price Filter Slider with Rounded Limits & Whole Numbers
                val rawMax = state.products.maxOfOrNull { it.price.toFloat() } ?: 500f
                // Round up to the nearest $50 increment (e.g., $342 becomes $350)
                val maxCatalogPrice = kotlin.math.ceil(rawMax / 50f) * 50f

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        // Use %.0f instead of %.2f to strip out the cents completely
                        text = "Max Price: $${
                            String.format(
                                "%.0f",
                                state.priceFilter ?: maxCatalogPrice
                            )
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.semantics { heading() }
                    )
                    Slider(
                        value = (state.priceFilter ?: maxCatalogPrice).toFloat(),
                        onValueChange = { onPriceChange(it.toDouble()) },
                        valueRange = 0f..maxCatalogPrice,
                        modifier = Modifier.semantics {
                            contentDescription = "Maximum product price"
                            stateDescription = "$${
                                String.format(
                                    "%.0f",
                                    state.priceFilter ?: maxCatalogPrice
                                )
                            } dollars"
                        }
                    )
                }
            }

            // 4. Action Buttons
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear All")
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}