package org.example.kmp.feature.catalog.presentation.details

import com.arkivanov.decompose.ComponentContext
import org.example.kmp.feature.catalog.domain.usecase.SyncProductDetailsUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase

interface ProductDetailsComponent {
    val productId: Int
    fun onBack()
}

class ProductDetailsComponentImpl(
    componentContext: ComponentContext,
    override val productId: Int,
    private val back: () -> Unit,
    private val syncProductDetailsUseCase: SyncProductDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ProductDetailsComponent, ComponentContext by componentContext {

    override fun onBack() = back()
}