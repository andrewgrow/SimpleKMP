package org.example.kmp.app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.kmp.feature.catalog.domain.usecase.ObserveCatalogUseCase
import org.example.kmp.feature.catalog.domain.usecase.ObserveFavoritesUseCase
import org.example.kmp.feature.catalog.domain.usecase.SyncCatalogPageUseCase
import org.example.kmp.feature.catalog.domain.usecase.SyncProductDetailsUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase
import org.example.kmp.feature.catalog.presentation.details.ProductDetailsComponent
import org.example.kmp.feature.catalog.presentation.details.ProductDetailsComponentImpl
import org.example.kmp.feature.catalog.presentation.favorites.FavoritesComponent
import org.example.kmp.feature.catalog.presentation.favorites.FavoritesComponentImpl
import org.example.kmp.feature.catalog.presentation.list.CatalogListComponent
import org.example.kmp.feature.catalog.presentation.list.CatalogListComponentImpl

class RootNavigation(
    componentContext: ComponentContext,
    private val childFactory: RootChildFactory,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Catalog,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    @OptIn(DelicateDecomposeApi::class)
    fun openDetails(productId: Int) {
        navigation.push(Config.Details(productId = productId))
    }

    @OptIn(DelicateDecomposeApi::class)
    fun openFavorites() {
        navigation.push(Config.Favorites)
    }

    fun back() {
        navigation.pop()
    }

    private fun createChild(
        config: Config,
        childContext: ComponentContext,
    ): RootComponent.Child {
        return when (config) {
            is Config.Catalog -> RootComponent.Child.Catalog(
                component = childFactory.createCatalog(
                    componentContext = childContext,
                    onOpenDetails = ::openDetails,
                    onOpenFavorites = ::openFavorites,
                )
            )

            is Config.Details -> RootComponent.Child.Details(
                component = childFactory.createDetails(
                    componentContext = childContext,
                    productId = config.productId,
                    onBack = ::back,
                )
            )

            is Config.Favorites -> RootComponent.Child.Favorites(
                component = childFactory.createFavorites(
                    componentContext = childContext,
                    onOpenDetails = ::openDetails,
                    onBack = ::back,
                )
            )
        }
    }

    @Serializable
    sealed interface Config {

        @Serializable
        @SerialName("catalog")
        data object Catalog : Config

        @Serializable
        @SerialName("details")
        data class Details(
            val productId: Int,
        ) : Config

        @Serializable
        @SerialName("favorites")
        data object Favorites : Config
    }
}

/**
 * Factory which creates child components with ComponentContext.
 * Later we will provide a real implementation via Koin.
 */
interface RootChildFactory {
    fun createCatalog(
        componentContext: ComponentContext,
        onOpenDetails: (Int) -> Unit,
        onOpenFavorites: () -> Unit,
    ): CatalogListComponent

    fun createDetails(
        componentContext: ComponentContext,
        productId: Int,
        onBack: () -> Unit,
    ): ProductDetailsComponent

    fun createFavorites(
        componentContext: ComponentContext,
        onOpenDetails: (Int) -> Unit,
        onBack: () -> Unit,
    ): FavoritesComponent
}

class DefaultRootChildFactory(
    private val observeCatalogUseCase: ObserveCatalogUseCase,
    private val syncCatalogPageUseCase: SyncCatalogPageUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val syncProductDetailsUseCase: SyncProductDetailsUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
) : RootChildFactory {

    override fun createCatalog(
        componentContext: ComponentContext,
        onOpenDetails: (Int) -> Unit,
        onOpenFavorites: () -> Unit,
    ): CatalogListComponent =
        CatalogListComponentImpl(
            componentContext = componentContext,
            openDetails = onOpenDetails,
            openFavorites = onOpenFavorites,
            observeCatalogUseCase = observeCatalogUseCase,
            syncCatalogPageUseCase = syncCatalogPageUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
        )

    override fun createDetails(
        componentContext: ComponentContext,
        productId: Int,
        onBack: () -> Unit,
    ): ProductDetailsComponent =
        ProductDetailsComponentImpl(
            componentContext = componentContext,
            productId = productId,
            back = onBack,
            syncProductDetailsUseCase = syncProductDetailsUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
        )

    override fun createFavorites(
        componentContext: ComponentContext,
        onOpenDetails: (Int) -> Unit,
        onBack: () -> Unit,
    ): FavoritesComponent =
        FavoritesComponentImpl(
            componentContext = componentContext,
            openDetails = onOpenDetails,
            back = onBack,
            observeFavoritesUseCase = observeFavoritesUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
        )
}