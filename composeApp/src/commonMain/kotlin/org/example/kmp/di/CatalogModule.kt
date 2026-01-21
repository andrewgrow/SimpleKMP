package org.example.kmp.di

import org.example.kmp.feature.catalog.data.mapper.ProductMappers
import org.example.kmp.feature.catalog.data.repository.CatalogRepositoryImpl
import org.example.kmp.feature.catalog.domain.repository.CatalogRepository
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
}