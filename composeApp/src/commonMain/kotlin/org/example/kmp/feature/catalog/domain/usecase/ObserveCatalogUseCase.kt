package org.example.kmp.feature.catalog.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.kmp.feature.catalog.domain.model.Product
import org.example.kmp.feature.catalog.domain.repository.CatalogRepository

class ObserveCatalogUseCase(
    private val repository: CatalogRepository,
) {

    operator fun invoke(
        pageIndex: Int,
        pageSize: Int,
    ): Flow<List<Product>> {
        require(pageIndex >= 0) { "pageIndex must be >= 0" }
        require(pageSize > 0) { "pageSize must be > 0" }

        return repository.observeCatalogPage(
            pageIndex = pageIndex,
            pageSize = pageSize,
        )
    }
}