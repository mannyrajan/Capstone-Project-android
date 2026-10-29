package com.pnc.capstoneprojectandroid.features.products.data

import com.pnc.capstoneprojectandroid.features.products.domain.Products

object ProductsMapper {
    fun mapToDomain(dto: ProductsDto): Products {
        return Products(
            id = dto.productId,
            name = dto.name,
            productNumber = dto.productNumber,
            summary = dto.summary.orEmpty(),
            thumbnailBase64 = dto.thumbnailPhoto,
            warranty = dto.warranty.orEmpty(),
            color = dto.color.orEmpty(),
            price = dto.listPrice
        )
    }

    fun mapDetailsToDomain(dto: ProductDetailsDto): Products {
        return Products(
            id = dto.productId,
            name = dto.productName.orEmpty(),
            productNumber = dto.productNumber.orEmpty(),
            summary = dto.summary.orEmpty(),
            thumbnailBase64 = dto.photo,
            warranty = listOfNotNull(dto.warrantyPeriod, dto.warrantyDescription)
                .filter { it.isNotBlank() }
                .joinToString(" - "),
            color = dto.color.orEmpty(),
            price = dto.listPrice ?: 0.0
        )
    }

}
