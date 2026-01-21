package org.example.kmp.feature.catalog.presentation.state

import org.example.kmp.feature.catalog.domain.model.Product
import org.example.kmp.feature.catalog.presentation.model.UiError

data class ProductDetailsState(
    val product: Product? = null,
    val error: UiError? = null,
)