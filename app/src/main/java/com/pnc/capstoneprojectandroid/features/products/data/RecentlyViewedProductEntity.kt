
package com.pnc.capstoneprojectandroid.features.products.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pnc.capstoneprojectandroid.features.products.domain.Products

@Entity(tableName = "recently_viewed_products")
data class RecentlyViewedProductEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val productNumber: String,
    val summary: String,
    val thumbnailBase64: String?,
    val warranty: String,
    val color: String,
    val price: Double,
    val viewedAt: Long
) {
    fun toProduct(): Products {
        return Products(
            id = id,
            name = name,
            productNumber = productNumber,
            summary = summary,
            thumbnailBase64 = thumbnailBase64,
            warranty = warranty,
            color = color,
            price = price
        )
    }

    companion object {
        fun fromProduct(product: Products): RecentlyViewedProductEntity {
            return RecentlyViewedProductEntity(
                id = product.id,
                name = product.name,
                productNumber = product.productNumber,
                summary = product.summary,
                thumbnailBase64 = product.thumbnailBase64,
                warranty = product.warranty,
                color = product.color,
                price = product.price,
                viewedAt = System.currentTimeMillis()
            )
        }
    }
}
