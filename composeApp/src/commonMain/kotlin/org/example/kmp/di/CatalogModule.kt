package org.example.kmp.di

import org.example.kmp.feature.catalog.data.mapper.ProductMappers
import org.example.kmp.feature.catalog.data.repository.CatalogRepositoryImpl
import org.example.kmp.feature.catalog.domain.repository.CatalogRepository
import org.example.kmp.feature.catalog.domain.usecase.ObserveCatalogUseCase
import org.example.kmp.feature.catalog.domain.usecase.ObserveFavoritesUseCase
import org.example.kmp.feature.catalog.domain.usecase.SyncCatalogPageUseCase
import org.example.kmp.feature.catalog.domain.usecase.SyncProductDetailsUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase
import org.koin.dsl.module
import kotlin.time.Clock.System.now

val catalogModule = module {
    single {
        ProductMappers()
    }

    single<CatalogRepository> {
        CatalogRepositoryImpl(
            api = get(),
            productDao = get(),
            favoriteDao = get(),
            mappers = get(),
            nowMillis = { now().toEpochMilliseconds() },
        )
    }

    factory { ObserveCatalogUseCase(get()) }
    factory { ObserveFavoritesUseCase(get()) }
    factory { SyncCatalogPageUseCase(get()) }
    factory { SyncProductDetailsUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
}