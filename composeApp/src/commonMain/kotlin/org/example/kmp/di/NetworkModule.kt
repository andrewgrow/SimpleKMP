package org.example.kmp.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.example.kmp.core.network.client.HttpClientFactory
import org.example.kmp.feature.catalog.api.CatalogApi
import org.example.kmp.feature.catalog.api.CatalogApiImpl
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        val engine: HttpClientEngine = get()
        HttpClientFactory.create(engine)
    }

    single<CatalogApi> {
        CatalogApiImpl(httpClient = get())
    }
}