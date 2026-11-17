package com.pnc.capstoneprojectandroid.ui.orderhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.capstoneprojectandroid.features.orders.data.api.OrderApiFactory
import com.pnc.capstoneprojectandroid.features.orders.data.model.CustomerOrder
import com.pnc.capstoneprojectandroid.features.orders.data.model.OrderShipping
import com.pnc.capstoneprojectandroid.features.orders.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

enum class ShipFilter { All, Shipped, Open }

data class OrderHistoryUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val query: String = "",
    val filter: ShipFilter = ShipFilter.All,
    val orders: List<CustomerOrder> = emptyList(),
    val selected: CustomerOrder? = null,
    val shipping: OrderShipping? = null,
    val detailLoading: Boolean = false,
    val detailError: String? = null,
) {
    val visibleOrders: List<CustomerOrder>
        get() {
            val q = query.trim()
            return orders.filter { order ->
                val matchesFilter = when (filter) {
                    ShipFilter.All -> true
                    ShipFilter.Shipped -> order.isShipped
                    ShipFilter.Open -> !order.isShipped
                }
                val matchesQuery = q.isEmpty() ||
                        order.orderNumber.toString().contains(q) ||
                        order.customerName.contains(q, ignoreCase = true) ||
                        order.customerId.toString().contains(q)
                matchesFilter && matchesQuery
            }
        }
}

class OrderHistoryViewModel @JvmOverloads constructor(
    private val repository: OrderRepository = OrderRepository(OrderApiFactory.create()),
) : ViewModel() {

    private val _state = MutableStateFlow(OrderHistoryUiState())
    val state: StateFlow<OrderHistoryUiState> = _state.asStateFlow()

    init {
        refresh(initial = true)
    }

    fun refresh(initial: Boolean = false) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = initial || it.orders.isEmpty(),
                    isRefreshing = !initial && it.orders.isNotEmpty(),
                    errorMessage = null,
                )
            }
            try {
                val allOrders = repository.getOrderHistory()
                val noahOrders = allOrders.filter { order ->
                    order.customerName.equals("Noah A Hall", ignoreCase = true)
                }
                _state.update { it.copy(isLoading = false, isRefreshing = false, orders = noahOrders) }
            } catch (error: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = error.toUserMessage(),
                    )
                }
            }
        }
    }

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun onFilterChange(filter: ShipFilter) {
        _state.update { it.copy(filter = filter) }
    }

    fun openOrder(order: CustomerOrder) {
        _state.update {
            it.copy(selected = order, shipping = null, detailError = null, detailLoading = true)
        }
        viewModelScope.launch {
            try {
                val shipping = repository.getShipping(order.representativeLineId)
                _state.update { it.copy(detailLoading = false, shipping = shipping) }
            } catch (error: Exception) {
                _state.update { it.copy(detailLoading = false, detailError = error.toUserMessage()) }
            }
        }
    }

    fun closeOrder() {
        _state.update { it.copy(selected = null, shipping = null, detailError = null) }
    }
}

private fun Throwable.toUserMessage(): String = when (this) {
    is UnknownHostException -> "No internet connection."
    is HttpException -> "The server returned HTTP ${code()}."
    else -> message?.takeIf { it.isNotBlank() } ?: "Couldn't load orders."
}