package org.example.kmp.feature.catalog.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.kmp.feature.catalog.domain.model.Product
import org.example.kmp.feature.catalog.domain.repository.CatalogRepository

class ObserveFavoritesUseCase(
    private val repository: CatalogRepository,
) {

    operator fun invoke(): Flow<List<Product>> {
        return repository.observeFavorites()
    }
}