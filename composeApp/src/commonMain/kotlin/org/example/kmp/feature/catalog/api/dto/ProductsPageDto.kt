package org.example.kmp.feature.catalog.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsPageDto(
    @SerialName("products") val products: List<ProductDto> = emptyList(),
    @SerialName("total") val total: Int = 0,
    @SerialName("skip") val skip: Int = 0,
    @SerialName("limit") val limit: Int = 0,
)