package org.example.kmp.di.test

import org.example.kmp.feature.catalog.domain.repository.CatalogRepository
import org.example.kmp.feature.catalog.domain.usecase.ObserveCatalogUseCase
import org.example.kmp.feature.catalog.domain.usecase.ObserveFavoritesUseCase
import org.example.kmp.feature.catalog.domain.usecase.ObserveProductDetailsUseCase
import org.example.kmp.feature.catalog.domain.usecase.SyncCatalogPageUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase
import org.koin.dsl.module

val testCatalogModule = module {

    single<CatalogRepository> { FakeCatalogRepository() }

    single { ObserveCatalogUseCase(get()) }
    single { ObserveFavoritesUseCase(get()) }
    single { ObserveProductDetailsUseCase(get()) }

    single { SyncCatalogPageUseCase(get()) }
    single { ToggleFavoriteUseCase(get()) }
}