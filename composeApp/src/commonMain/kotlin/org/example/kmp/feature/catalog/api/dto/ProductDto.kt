package org.example.kmp.feature.catalog.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("id") val id: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("price") val price: Double = 0.0,
    @SerialName("thumbnail") val thumbnailUrl: String = "",
    @SerialName("images") val images: List<String> = emptyList(),
)