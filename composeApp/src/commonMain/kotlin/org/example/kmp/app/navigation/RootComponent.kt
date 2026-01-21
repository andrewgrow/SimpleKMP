package org.example.kmp.app.navigation

import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.router.stack.ChildStack
import org.example.kmp.feature.catalog.presentation.details.ProductDetailsComponent
import org.example.kmp.feature.catalog.presentation.favorites.FavoritesComponent
import org.example.kmp.feature.catalog.presentation.list.CatalogListComponent

class RootComponent(
    private val navigation: RootNavigation,
) {
    val childStack: Value<ChildStack<*, Child>> = navigation.childStack

    fun onBackClicked() {
        navigation.back()
    }

    sealed interface Child {
        data class Catalog(val component: CatalogListComponent) : Child
        data class Details(val component: ProductDetailsComponent) : Child
        data class Favorites(val component: FavoritesComponent) : Child
    }
}