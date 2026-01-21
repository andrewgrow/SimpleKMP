package org.example.kmp.feature.catalog.presentation.favorites

import com.arkivanov.decompose.ComponentContext
import org.example.kmp.feature.catalog.domain.usecase.ObserveFavoritesUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase

interface FavoritesComponent {
    fun onOpenDetails(productId: Int)
    fun onBack()
}

class FavoritesComponentImpl(
    componentContext: ComponentContext,
    private val openDetails: (Int) -> Unit,
    private val back: () -> Unit,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : FavoritesComponent, ComponentContext by componentContext {

    override fun onOpenDetails(productId: Int) = openDetails(productId)
    override fun onBack() = back()
}