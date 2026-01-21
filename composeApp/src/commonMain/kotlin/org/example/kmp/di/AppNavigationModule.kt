package org.example.kmp.di

import org.example.kmp.app.navigation.DefaultRootChildFactory
import org.example.kmp.app.navigation.RootChildFactory
import org.koin.dsl.module

val appNavigationModule = module {
    single<RootChildFactory> {
        DefaultRootChildFactory(
            observeCatalogUseCase = get(),
            syncCatalogPageUseCase = get(),
            toggleFavoriteUseCase = get(),
            syncProductDetailsUseCase = get(),
            observeFavoritesUseCase = get(),
        )
    }
}