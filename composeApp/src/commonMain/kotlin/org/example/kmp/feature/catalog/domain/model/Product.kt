package org.example.kmp.feature.catalog.domain.model

data class Product(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: Int = 0,
    val thumbnailUrl: String = "",
    val images: List<String> = emptyList(),
    val isFavorite: Boolean = false,
)