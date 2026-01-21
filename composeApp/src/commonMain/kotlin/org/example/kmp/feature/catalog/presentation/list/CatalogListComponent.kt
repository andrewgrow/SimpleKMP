package org.example.kmp.feature.catalog.presentation.list

import com.arkivanov.decompose.ComponentContext
import org.example.kmp.feature.catalog.domain.usecase.ObserveCatalogUseCase
import org.example.kmp.feature.catalog.domain.usecase.SyncCatalogPageUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase

interface CatalogListComponent {
    fun onOpenDetails(productId: Int)
    fun onOpenFavorites()
}

class CatalogListComponentImpl(
    componentContext: ComponentContext,
    private val openDetails: (Int) -> Unit,
    private val openFavorites: () -> Unit,
    private val observeCatalogUseCase: ObserveCatalogUseCase,
    private val syncCatalogPageUseCase: SyncCatalogPageUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : CatalogListComponent, ComponentContext by componentContext {

    override fun onOpenDetails(productId: Int) = openDetails(productId)
    override fun onOpenFavorites() = openFavorites()
}