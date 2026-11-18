package com.pnc.capstoneprojectandroid.features.inventory.domain

enum class ProductAvailability {
    IN_STOCK,
    LOW_STOCK,
    UNAVAILABLE,
    UNKNOWN
}

object ProductAvailabilityClassifier {

    fun classify(
        quantity: Int?,
        reorderPoint: Int?
    ): ProductAvailability {

        if (quantity == null || reorderPoint == null) {
            return ProductAvailability.UNKNOWN
        }

        if (quantity < 0 || reorderPoint < 0) {
            return ProductAvailability.UNKNOWN
        }

        return when {
            quantity == 0 -> ProductAvailability.UNAVAILABLE
            quantity <= reorderPoint -> ProductAvailability.LOW_STOCK
            else -> ProductAvailability.IN_STOCK
        }
    }

    fun displayLabel(availability: ProductAvailability): String {
        return when (availability) {
            ProductAvailability.IN_STOCK -> "In Stock"
            ProductAvailability.LOW_STOCK -> "Low Stock"
            ProductAvailability.UNAVAILABLE -> "Unavailable"
            ProductAvailability.UNKNOWN -> "Availability Unknown"
        }
    }
}
