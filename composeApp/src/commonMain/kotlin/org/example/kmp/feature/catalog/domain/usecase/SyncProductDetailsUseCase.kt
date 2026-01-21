package org.example.kmp.feature.catalog.domain.usecase

import org.example.kmp.feature.catalog.domain.repository.CatalogRepository

class SyncProductDetailsUseCase(
    private val repository: CatalogRepository,
) {

    suspend operator fun invoke(productId: Int) {
        require(productId > 0) { "productId must be > 0" }

        repository.syncProductDetails(productId)
    }
}