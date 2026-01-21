package org.example.kmp.feature.catalog.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.kmp.feature.catalog.domain.model.Product
import org.example.kmp.feature.catalog.domain.repository.CatalogRepository

class ObserveProductDetailsUseCase(
    private val repository: CatalogRepository,
) {
    operator fun invoke(productId: Int): Flow<Product?> {
        return repository.observeProductDetails(productId)
    }
}